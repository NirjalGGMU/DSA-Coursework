//Q.N6 - Web Crawler 

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
        Deque<Future> tasks = new ArrayDeque<>();

        queue.offer(startUrl);

        // Create a thread pool of 4 threads to perform I/O operations.
        ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            // Leetcode doesn't allow executor.shutdown().
            // Use daemon threads so the program can exit.
            t.setDaemon(true);
            return t;
        });

        while (true) {
            String url = queue.poll();
            if (url != null) {
                if (getHostName(url).equals(hostName) && !visited.contains(url)) {
                    res.add(url);
                    visited.add(url);
                    // Use a thread in thread pool to fetch new URLs and put them into the queue.
                    tasks.add(executor.submit(() -> {
                        List<String> newUrls = htmlParser.getUrls(url);
                        for (String newUrl : newUrls) {
                            queue.offer(newUrl);
                        }
                    }));
                }
            } else {
                if (!tasks.isEmpty()) {
                    // Wait for the next task to complete, which may supply new URLs into the queue.
                    Future nextTask = tasks.poll();
                    try {
                        nextTask.get();
                    } catch (InterruptedException | ExecutionException e) {}
                } else {
                    // Exit when all tasks are completed.
                    break;
                }
            }
        }
        return res;
    }
    
    private String getHostName(String url) {
        url = url.substring(7);
        String[] parts = url.split("/");
        return parts[0];
    }
}


// Output: 
// Test Case 1 - Start with yahoo.com:
// Crawled URLs: [http://news.yahoo.com, http://news.yahoo.com/news, http://news.yahoo.com/us, http://news.yahoo.com/news/topics/]

// Test Case 2 - Start with google.com:
// Crawled URLs: [http://news.google.com]