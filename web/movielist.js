function handleMovielistResult(movies) {
    let movieTable = $("#movie-table");

    for (let i = 0; i < movies.length; i++) {
        let movieId = movies[i]["movie_id"];
        let movieTitle = movies[i]["movie_title"];
        let movieYear = movies[i]["movie_year"];
        let movieDirector = movies[i]["movie_director"];
        let movieGenres = movies[i]["movie_genres"];
        let movieStars = movies[i]["movie_stars"];
        let movieRating = movies[i]["movie_rating"];

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

        rowHTML += "<td>" + movieRating + "/10</td>";
        rowHTML += "</tr>";

        movieTable.append(rowHTML);
    }
}

function getUrlParam(param, defaultValue) {
    let searchParams = new URLSearchParams(window.location.search)
    let value = searchParams.get(param);
    if (value === null) {
        return defaultValue;
    }

    return value;
}

function populateYearOptions() {
    let selectTag = $("#year");

    let MIN_YEAR = 2000;
    let MAX_YEAR = 2020;

    for (let i = MAX_YEAR; i >= MIN_YEAR; i--) {
        selectTag.append("<option>" + i + "</option>");
    }
}

function determineQueryParameters() {
    let title = getUrlParam("title", "");
    let year = getUrlParam("year", 0);
    let director = getUrlParam("director", "");
    let star = getUrlParam("star", "");
    let genre = getUrlParam("genre", 0);
    let limit = getUrlParam("limit", 10);
    let page = getUrlParam("page", 1);
    let sortBy = getUrlParam("sortBy", "rating_desc");

    // send query to backend
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movielist",
        data: {
            "title": title,
            "year": year,
            "director": director,
            "star": star,
            "genre": genre,
            "limit": limit,
            "page": page,
            "sortBy": sortBy
        },
        success: (resultData) => handleMovielistResult(resultData)
    });
}


populateYearOptions();
determineQueryParameters();
