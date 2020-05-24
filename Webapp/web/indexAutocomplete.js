

let cachedMovieResults;
if (!window.localStorage.getItem("cachedMovieResults")) {
    cachedMovieResults = {};
}
else {
    cachedMovieResults = JSON.parse(window.localStorage.getItem("cachedMovieResults"));
}

function handleLookupAjaxSuccess(data, query, done) {
    console.log("AJAX lookup successful");

    let json = JSON.parse(data);

    let suggestions = [];

    for (let i = 0; i < json.length; i++) {
        let movieId = json[i]["movie_id"];
        let movieTitle = json[i]["movie_title"];
        let movieYear = json[i]["movie_year"];

        let newTitle = formatMovieTitleAndYear(movieTitle, movieYear);
        suggestions.push({"value": newTitle, "data": movieId});
    }

    console.log("Results: " + JSON.stringify(suggestions));

    // TODO: if you want to cache the result into a global variable you can do it here
    cachedMovieResults[query] = suggestions;
    window.localStorage.setItem("cachedMovieResults", JSON.stringify(cachedMovieResults));

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    done({"suggestions": suggestions});
}

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, done) {
    console.log("Autocomplete initiated.")

    // TODO: if you want to check past query results first, you can do it here
    let previousResult = cachedMovieResults[query];

    // if there are cached results, return them
    if (previousResult !== undefined) {
        console.log("Using cached results.");
        console.log("Cached results: " + JSON.stringify(previousResult));
        done({suggestions: previousResult});
        return;
    }

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    console.log("Sending AJAX request to backend Java Servlet.")
    $.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/autocomplete?",
        "data": {"q": query},
        "success": function(data) {
            // pass the data, query, and done function into the success handler
            handleLookupAjaxSuccess(data, query, done)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData);
        }
    })
}

function handleSelectSuggestion(suggestion) {
    let movieId = suggestion.data;
    let url = "movie.html?id=" + movieId;
    window.location.href = url;
}

$("#main_search_bar_input").autocomplete({
    lookup: function (query, done) {
        handleLookup(query, done);
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion);
    },
    minChars: 3,
    deferRequestBy: 300,
    preserveInput: true
});