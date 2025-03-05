// Q.N6 - Web Crawler 

/*
Algorithm:
1. Extract the hostname from the given start URL.
2. Use a queue to manage URLs that need to be crawled.
3. Use a set to track visited URLs to avoid duplicate processing.
4. Create a fixed thread pool (4 threads) to perform concurrent crawling.
5. Process each URL:
   - If it matches the hostname and hasn't been visited, add it to the result list.
   - Fetch new URLs using the HtmlParser and add them to the queue.
6. Wait for all tasks to complete and return the list of crawled URLs.
*/

import java.util.*;
import java.util.concurrent.*;

interface HtmlParser {
    List<String> getUrls(String url);
}

public class WebCrawler {
    public static void main(String[] args) {
        HtmlParser parser = new HtmlParser() {
            private Map<String, List<String>> urlMap = new HashMap<>();
            {
                // Configure URL relationships
                urlMap.put("http://news.yahoo.com", Arrays.asList(
                    "http://news.yahoo.com/news",
                    "http://news.yahoo.com/us"
                ));
                urlMap.put("http://news.yahoo.com/news", Arrays.asList(
                    "http://news.yahoo.com/news/topics/"
                ));
                urlMap.put("http://news.yahoo.com/news/topics/", Collections.emptyList());
                urlMap.put("http://news.google.com", Collections.emptyList());
                urlMap.put("http://news.yahoo.com/us", Collections.emptyList());
            }

            @Override
            public List<String> getUrls(String url) {
                return urlMap.getOrDefault(url, Collections.emptyList());
            }
        };

        // Create web crawler instance
        WebCrawler crawler = new WebCrawler();
        
        // Test with different start URLs
        System.out.println("Test Case 1 - Start with yahoo.com:");
        List<String> result1 = crawler.crawl("http://news.yahoo.com", parser);
        System.out.println("Crawled URLs: " + result1);

        System.out.println("\nTest Case 2 - Start with google.com:");
        List<String> result2 = crawler.crawl("http://news.google.com", parser);
        System.out.println("Crawled URLs: " + result2);
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String hostName = getHostName(startUrl);

        List<String> res = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Deque<Future<?>> tasks = new ArrayDeque<>();

        queue.offer(startUrl);

        // Create a thread pool of 4 threads to perform concurrent crawling
        ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); // Use daemon threads so the program can exit automatically
            return t;
        });

        while (true) {
            String url = queue.poll();
            if (url != null) {
                // Check if URL belongs to the same hostname and hasn't been visited
                if (getHostName(url).equals(hostName) && visited.add(url)) {
                    res.add(url);
                    // Use a thread in the pool to fetch new URLs
                    tasks.add(executor.submit(() -> {
                        List<String> newUrls = htmlParser.getUrls(url);
                        queue.addAll(newUrls);
                    }));
                }
            } else {
                if (!tasks.isEmpty()) {
                    // Wait for the next task to complete
                    Future<?> nextTask = tasks.poll();
                    try {
                        nextTask.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Exit when all tasks are completed
                    break;
                }
            }
        }

        executor.shutdown(); // Shutdown thread pool after execution
        return res;
    }

    // Extract hostname from a given URL
    private String getHostName(String url) {
        url = url.substring(7); // Remove "http://"
        String[] parts = url.split("/");
        return parts[0]; // Extract domain name
    }
}

// Output: 
// Test Case 1 - Start with yahoo.com:
// Crawled URLs: [http://news.yahoo.com, http://news.yahoo.com/news, http://news.yahoo.com/us, http://news.yahoo.com/news/topics/]

// Test Case 2 - Start with google.com:
// Crawled URLs: [http://news.google.com]
