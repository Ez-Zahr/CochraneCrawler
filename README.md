# The Cochrane Library crawler

The solution uses Apache HttpClient5 fluent API. This was used for a more compact and readable code, as opposed to using the classic or async approach that involves customizing the request. Since we don’t need that kind of flexibility, fluent requests are more suitable in this case.

The first thing I do is define a FileWrite object in a try-with-resources statement. The use of the try-with-resources statement is essential to make sure the FileWriter resource is closed at the end of the try-catch block, whether it exited normally or abruptly due to an exception.

Then, a request is made to retrieve the HTML content of the Cochrane Library topics page. That content is used to initialize a Jsoup Document object. Jsoup is a library that helps with parsing HTML content in a simple and efficient way. From the Document object, I extract all of the elements that contain the topics and URL links where each topic’s reviews are located.

In a for-each loop that iterates over the topics elements, I extract the name of the topic and the link to its reviews. Then, I start another level of crawling, where I crawl each link to get all of a topic’s reviews. I start by making a request to get the content of the link and construct a Jsoup Document object. Next, I get all of the elements that appear in the search results body, and I iterate over these elements, extracting the URL, title, author, and date to write that to the output file.

After extracting all of the reviews in the current page, I check to see if there’s a “Next” button at the bottom for more reviews in other pages, as a page only shows 25 reviews maximum. If that’s the case, I get the target link of that button, load the new page’s content into a Jsoup Document object, and rerun the algorithm for extracting the reviews elements and writing to the output file.

The program is designed to repeat the process for each topic extracted from the library. For the purposes of this assignment, I terminate after the first iteration and only crawl the first topic I find.

The crawler is designed in a way that makes it scalable for however many topics or reviews there are. It also performs accurately, because it targets the specific containers and elements that have the data we want. Furthermore, utilizing libraries such as Apache HttpClient fluent and Jsoup, allows for a cleaner and more reusable code that effectively accomplishes our main goals. Also, The practices used to manage resources guarantee a safe environment, where resources are handled properly.

Additional features could be added to support a more detailed search of a review given its ID, using the Cochrane REST API.
