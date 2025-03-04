/**
 * Web Crawler Algorithm:
 Step 1: Define a thread pool to manage concurrency with a fixed size of THREAD_POOL_SIZE (e.g., 5 threads).
 Step 2: Create a Set (visitedUrls) to keep track of visited URLs to prevent duplication.
 Step 3: Add the seed URL to the visited set (e.g., "https://example.com").
 Step 4: Submit the initial crawling task (CrawlTask) for the seed URL to the thread pool.
 
 * CrawlTask Execution:
 Step 5: Fetch the content of the web page from the given URL.
 Step 6: Extract the title from the HTML content (between <title> and </title> tags).
 Step 7: Extract all URLs from the page by searching for the "href" attributes in the HTML content.
 Step 8: For each extracted URL:
    a. Check if the URL has already been visited.
    b. If the URL has not been visited, add it to the visited set.
    c. Submit a new CrawlTask for the extracted URL to the thread pool.
 Step 9: Repeat the crawling process for new URLs within the set time limit (e.g., 5 seconds).
 Step 10: Shutdown the thread pool after the crawling time limit is reached to stop further crawling.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Multi-threaded web crawler that fetches web pages, extracts links, and crawls new pages.
 */
public class WebCrawler {
    private static final int THREAD_POOL_SIZE = 5; // Number of concurrent threads
    private static final Set<String> visitedUrls = new HashSet<>(); // Set to track visited URLs (to prevent duplication)
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE); // Thread pool to manage concurrency

    public static void main(String[] args) {
        // Define the seed URL to start crawling
        String seedUrl = "https://example.com";
        
        // Add the seed URL to the visited set to avoid recrawling
        visitedUrls.add(seedUrl);
        
        // Submit the first crawling task to the thread pool
        threadPool.submit(new CrawlTask(seedUrl));

        // Allow crawling for a limited time (5 seconds), then shut down
        try {
            Thread.sleep(5000); // Wait for 5 seconds before shutting down
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Shutdown the thread pool after crawling
        threadPool.shutdown();
    }

    /**
     * Inner class that defines the crawling task.
     */
    static class CrawlTask implements Runnable {
        private final String url; // URL to crawl

        public CrawlTask(String url) {
            this.url = url; // Initialize with the given URL
        }

        @Override
        public void run() {
            try {
                // Fetch the web page content
                String content = fetchWebPage(url);

                // Extract and print the page title
                String title = extractTitle(content);
                System.out.println("Crawled: " + url + " -> Title: " + title);

                // Extract new URLs from the page
                Set<String> newUrls = extractUrls(content);

                // Submit new URLs for crawling
                for (String newUrl : newUrls) {
                    synchronized (visitedUrls) { // Ensure thread-safe access to shared data
                        if (!visitedUrls.contains(newUrl)) { // Check if URL is already visited
                            visitedUrls.add(newUrl); // Mark URL as visited
                            threadPool.submit(new CrawlTask(newUrl)); // Submit a new crawl task for this URL
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error crawling " + url + ": " + e.getMessage());
            }
        }

        /**
         * Fetches the content of a web page from the given URL.
         */
        private String fetchWebPage(String url) throws IOException {
            // Open an HTTP connection to the URL
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET"); // Set HTTP method to GET

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;

            // Read line by line and append to content
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close(); // Close the reader
            
            return content.toString(); // Return the fetched content
        }

        /**
         * Extracts the title from the HTML content.
         */
        private String extractTitle(String content) {
            int titleStart = content.indexOf("<title>"); // Find the start index of <title> tag
            int titleEnd = content.indexOf("</title>"); // Find the end index of </title> tag
            
            if (titleStart != -1 && titleEnd != -1) {
                return content.substring(titleStart + 7, titleEnd).trim(); // Extract and return title
            }
            return "No Title"; // Return default value if no title found
        }

        /**
         * Extracts URLs from the HTML content by looking for 'href' attributes.
         */
        private Set<String> extractUrls(String content) {
            Set<String> urls = new HashSet<>(); // Set to store unique URLs
            int hrefStart;
            int hrefEnd = 0;
            
            // Loop to find all occurrences of href="URL"
            while ((hrefStart = content.indexOf("href=\"", hrefEnd)) != -1) {
                hrefEnd = content.indexOf("\"", hrefStart + 6); // Find the closing quote of href
                if (hrefEnd != -1) {
                    String newUrl = content.substring(hrefStart + 6, hrefEnd); // Extract the URL
                    
                    // Only add valid absolute URLs
                    if (newUrl.startsWith("http")) {
                        urls.add(newUrl);
                    }
                }
            }
            return urls; // Return the set of extracted URLs
        }
    }
}

// Output
// Crawled: https://example.com -> Title: Example Domain
// Crawled: https://www.iana.org/domains/example -> Title: 301 Moved Permanently
// Crawled: http://www.iana.org/help/example-domains -> Title: Example Domains
// Error crawling http://www.icann.org/: Server returned HTTP response code: 403 for URL: http://www.icann.org/
// Crawled: https://www.icann.org/privacy/tos -> Title: TERMS OF SERVICE - ICANN
// Error crawling https://www.icann.org/privacy/tos: Task java.util.concurrent.FutureTask@54085258[Not completed, task = java.util.concurrent.Executors$RunnableAdapter@7b2ae49e[Wrapped task = WebCrawler$CrawlTask@3758610a]] rejected from java.util.concurrent.ThreadPoolExecutor@27716f4[Shutting down, pool size = 3, active threads = 3, queued tasks = 0, completed tasks = 4]
// Crawled: https://www.icann.org/privacy/policy -> Title: PRIVACY POLICY - ICANN
// Error crawling https://www.icann.org/privacy/policy: Task java.util.concurrent.FutureTask@2221e2e5[Not completed, task = java.util.concurrent.Executors$RunnableAdapter@667b2c4f[Wrapped task = WebCrawler$CrawlTask@52fd126a]] rejected from java.util.concurrent.ThreadPoolExecutor@27716f4[Shutting down, pool size = 2, active threads = 2, queued tasks = 0, completed tasks = 5]
// Error crawling http://pti.icann.org: Connection refused: connect