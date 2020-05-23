let cachedResults = {};

function handleLookupAjaxSuccess(data, query, done) {
    console.log("lookup ajax successful");

    let json = JSON.parse(data);

    let suggestions = [];

    for (let i = 0; i < json.length; i++) {
        let movieId = json[i]["movie_id"];
        let movieTitle = json[i]["movie_title"];
        let movieYear = json[i]["movie_year"];

        let newTitle = formatMovieTitleAndYear(movieTitle, movieYear);
        suggestions.push({"value": newTitle, "data": movieId});
    }

    // TODO: if you want to cache the result into a global variable you can do it here

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
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
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

$("#main_search_bar_input").autocomplete({
    lookup: function (query, done) {
        handleLookup(query, done);
    },
    onSelect: function(suggestion) {
        alert(suggestion.data);
    }
})