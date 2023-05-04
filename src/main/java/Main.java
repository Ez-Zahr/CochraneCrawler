import org.apache.hc.client5.http.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static final String BASE_URL = "https://www.cochranelibrary.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";

    public static void main(String[] args) {
        // Using a try-with-resources to open a stream for writing and ensure it being closed at the end
        try (FileWriter writer = new FileWriter("cochrane_reviews.txt")) {
            // Getting all the topics to crawl for reviews
            String content = Request.get(BASE_URL + "/cdsr/reviews/topics")
                    .userAgent(USER_AGENT).execute().returnContent().asString();

            Document doc = Jsoup.parse(content);
            Elements topics = doc.select("#portlet_scolaristopics_WAR_scolaristopics")
                    .select(".portlet-content").select(".container").select("a");

            for (Element topic : topics) {
                String topicName = topic.text();
                String topicLink = topic.attr("href");

                // Crawling a specific topic
                String topicContent = Request.get(topicLink).userAgent(USER_AGENT).execute().returnContent().asString();
                Document topicDoc = Jsoup.parse(topicContent);

                while (true) {
                    // Getting all reviews in a specific topic
                    Elements reviews = topicDoc.select(".search-results-section-body")
                            .select(".search-results-item-body");

                    for (Element review : reviews) {
                        Elements reviewTitleLink = review.select(".result-title").select("a");
                        String reviewLink = BASE_URL + reviewTitleLink.attr("href");
                        String reviewTitle = reviewTitleLink.text();
                        String reviewAuthors = review.select(".search-result-authors").text();
                        String reviewDate = review.select(".search-result-date").text();
                        writer.write(reviewLink + " | " + topicName + " | " + reviewTitle + " | "
                                + reviewAuthors + " | " + reviewDate + "\n\n");
                    }

                    // If there's a next page with more reviews, crawl the content of that page and rerun the loop
                    Elements next = topicDoc.select(".search-results-footer").select(".pagination-next-link").select("a");
                    if (!next.text().equals("Next")) break;

                    String nextLink = next.attr("href");
                    topicContent = Request.get(nextLink).userAgent(USER_AGENT).execute().returnContent().asString();
                    topicDoc = Jsoup.parse(topicContent);
                }

                // Break out of the loop after the first iteration. We will only get the first topic's reviews.
                // Remove the break statement if you want to crawl all topics.
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
