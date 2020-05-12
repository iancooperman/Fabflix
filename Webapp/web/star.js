function getUrlParam(param) {
    let searchParams = new URLSearchParams(window.location.search)
    return searchParams.get(param);
}

function handleStarResult(resultData) {
    let starName = resultData["star_name"];
    let birthYear = resultData["star_birth_year"];
    if (birthYear === null) {
        birthYear = "N/A";
    }
    let filmography = resultData["filmography"];

    document.title = starName;
    $("#name").text(starName);
    $("#year-of-birth").text("Born: " + birthYear);

    let tableInnerHTML = "";
    for (let i = 0; i < filmography.length; i++) {
        let movieId = filmography[i]["movie_id"];
        let movieTitle = filmography[i]["movie_title"];
        tableInnerHTML += "<tr><td><a href='movie.html?id=" + movieId + "'>" + movieTitle + "</a></td></tr>";
    }
    $("#filmography-table").html(tableInnerHTML);

    // set "Back to Movies" URL
    let movielistParameters = resultData["movielist_parameters"];
    let backURL = "movielist.html?" + $.param(movielistParameters);
    $("#movie-list-link").attr("href", backURL);

}


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/star?id=" + getUrlParam("id"),
    success: (resultData) => handleStarResult(resultData)
});