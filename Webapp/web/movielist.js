let listings;

function handleMovielistResult(movieData) {
    let movieTable = $("#movie-table");

    listings = movieData["row_count"];
    setUpPageButtons();

    let movies = movieData["movies"];

    for (let i = 0; i < movies.length; i++) {
        let movieId = movies[i]["movie_id"];
        let movieTitle = movies[i]["movie_title"];
        let movieYear = movies[i]["movie_year"];
        let movieDirector = movies[i]["movie_director"];
        let movieGenres = movies[i]["movie_genres"];
        let movieStars = movies[i]["movie_stars"];
        let movieRating = movies[i]["movie_rating"];
        let moviePrice = movies[i]["movie_price"];

        if (movieRating === null) {
            movieRating = "N/A";
        }

        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<td><a href=\"movie.html?id=" + movieId + "\">" + movieTitle + " (" + movieYear + ")" + "</a></td>";
        rowHTML += "<td>" + movieDirector + "</td>";

        // genres
        rowHTML += "<td><ul>";
        for (let j = 0; j < movieGenres.length; j++) {
            rowHTML += "<li><a href='movielist.html?genre=" + movieGenres[j]["genre_id"] + "'>" + movieGenres[j]["genre_name"] + "</a></li>"
        }
        rowHTML += "</ul></td>"

        // stars
        rowHTML += "<td><ul>";
        for (let j = 0; j < movieStars.length; j++) {
            rowHTML += "<li><a href='star.html?id=" + movieStars[j]["star_id"] + "'>" + movieStars[j]["star_name"] + "</a></li>"
        }
        rowHTML += "</ul></td>"

        // Rating
        rowHTML += "<td>" + movieRating + "/10</td>";

        // Price
        rowHTML += "<td><button class='btn btn-info price_button'>" + centsToDollars(moviePrice) + "</button></td>";

        // end the row
        rowHTML += "</tr>";
        movieTable.append(rowHTML);

        // add click listener to the newly created price button
        $(".price_button").last().click(function() {addToCart(movieId);});
    }
}

function handleCartResult(resultData) {
    console.log(resultData);
    alert(resultData["message"]);
}

function addToCart(movieId) {

    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/addToCart",
        data: {"id": movieId},
        success: (resultData) => handleCartResult(resultData)
    })
}

function getUrlParam(param, defaultValue) {
    let searchParams = new URLSearchParams(window.location.search)
    let value = searchParams.get(param);
    if (value === null) {
        return defaultValue;
    }

    return value;
}

function determineQueryParameters() {
    let q = getUrlParam('q', "");
    let title = getUrlParam("title", "");
    let year = getUrlParam("year", 0);
    let director = getUrlParam("director", "");
    let star = getUrlParam("star", "");
    let genre = getUrlParam("genre", 0);
    let limit = getUrlParam("limit", 10);
    let page = getUrlParam("page", 1);
    let sortBy = getUrlParam("sortBy", "rating_desc_title_asc");
    let cp = getUrlParam("cp", "true");

    // send query to backend
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movielist",
        data: {
            "q": q,
            "title": title,
            "year": year,
            "director": director,
            "star": star,
            "genre": genre,
            "limit": limit,
            "page": page,
            "sortBy": sortBy,
            "cp": cp
        },
        success: (resultData) => handleMovielistResult(resultData)
    });
}

function setUpPageButtons() {
    let page = getUrlParam("page", "1");

    let q = getUrlParam("q", "");
    let title = getUrlParam("title", "");
    let year = getUrlParam("year", 0);
    let director = getUrlParam("director", "");
    let star = getUrlParam("star", "");
    let genre = getUrlParam("genre", 0);
    let limit = getUrlParam("limit", 10);
    let sortBy = getUrlParam("sortBy", "rating_desc_title_asc");

    if (page === "1") {
        $("#prev-button").remove();
    }
    else {
        let prevURL = "movielist.html?" + $.param({"q": q, "page": Number(page) - 1, "title": title, "year": year, "director": director, "star": star, "genre": genre, "limit": limit, "sortBy": sortBy});
        $("#prev-button").attr("href", prevURL);
    }


    if (isLastPage()) {
        $("#next-button").remove();
    }
    else {
        let nextURl = "movielist.html?" + $.param({"q": q, "page": Number(page) + 1, "title": title, "year": year, "director": director, "star": star, "genre": genre, "limit": limit, "sortBy": sortBy});
        $("#next-button").attr("href", nextURl);
    }
}

function isLastPage() {
    let pageNumber = Number(getUrlParam("page", 1));
    let limit = Number(getUrlParam("limit", 10));
    let results = Number(listings);
    return ((limit * (pageNumber + 1)) > results);
}

function reloadWithNewParams(eventObject) {
    eventObject.preventDefault();

    let q = getUrlParam("q", "");
    let page = getUrlParam("page", "1");
    let title = getUrlParam("title", "");
    let year = getUrlParam("year", 0);
    let director = getUrlParam("director", "");
    let star = getUrlParam("star", "");
    let genre = getUrlParam("genre", 0);

    let limit = $("#entries-per-page").val();
    let sortBy = $("#sort-by").val();

    let newURL = "movielist.html?" + $.param({"q": q, "page": Number(page), "title": title, "year": year, "director": director, "star": star, "genre": genre, "limit": limit, "sortBy": sortBy});

    window.location.href = newURL;

}

function setFormValues() {
    let limit = getUrlParam("limit", 10);
    let sortBy = getUrlParam("sortBy", "rating_desc_title_asc");

    $("#entries-per-page").val(limit);
    $("#sort-by").val(sortBy);
}

$("#adjust-form").submit(reloadWithNewParams);
determineQueryParameters();
setFormValues();

