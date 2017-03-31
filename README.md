WPIAL
=====

website to display WPIAL statistics

<a href="http://patmyron.com/wpial">www.patmyron.com/wpial</a>

<p>
    Two dropdown menus gather input for the user's choice of school and sport.
    <br>
    In addition, the user can choose "all" for schools or sports.
    <ol>
        <li>
            The user can pick a specific school and sport to see that team's statistics.
        </li>
        <li>
            Choosing "all" for schools and choosing a specific sport will list the statistics for all schools that offer that sport.
        </li>
        <li>
            Choosing "all" for sports and choosing a specific school will list the statistics for all the sports that school offers.
        </li>    
    </ol>
</p>

<img src="https://raw.githubusercontent.com/PatMyron/wpial/gh-pages/photos/example.png" alt="wpial statistics website screenshot" width="100%" height="auto">

Technical Details
-----------------
<ul>
<li>Scraped data from a local website (<a href="http://post-gazette.com">post-gazette.com</a>) using Jsoup’s API</li>
<li>Data was analyzed in Java and statistics were written to HTML files in table format</li>
<li>Website takes multiple inputs through drop-down menus and displays correct table by
calling the HTML file containing the table using JavaScript</li>
</ul>
