function getUrlParam(param) {
    let searchParams = new URLSearchParams(window.location.search)
    return searchParams.get(param);
}

function handleMovieResult(resultData) {
    document.title = resultData["movie_title"];
    $("#row-1").text(resultData["movie_title"]+ " (" + resultData["movie_year"] + ") | " + resultData["movie_rating"] + "/10");
    $("#row-3").text("Director: " + resultData["movie_director"]);

    let genresHTML = ""
    for (let i = 0; i < resultData["genres"].length; i++) {
        genresHTML += resultData["genres"][i] + ", ";
    }
    genresHTML = genresHTML.substring(0, genresHTML.length - 2);

    $("#row-2").html(genresHTML);

    let starsHTML = "Stars: "
    for (let i = 0; i < resultData["stars"].length; i++) {
        starsHTML += "<a href='star.html?id=" + resultData["stars"][i]["star_id"] + "'>" + resultData["stars"][i]["star_name"] + "</a>, ";
    }
    starsHTML = starsHTML.substring(0, starsHTML.length - 2);

    $("#row-4").html(starsHTML);
}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie?id=" + getUrlParam("id"),
    success: (resultData) => handleMovieResult(resultData)
});