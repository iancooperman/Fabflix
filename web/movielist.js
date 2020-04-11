



function handleMovielistResult(resultData) {
    console.log("This is getting called, yes?");


    let movieTable = $("#movie-table");

    for (let i = 0; i < resultData.length; i++) {

        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<td><a href=\"movie.html?id=" + resultData[i]["movie_id"] + "\">" + resultData[i]["movie_title"] + " (" + resultData[i]["movie_year"] + ")" + "</a></td>";
        console.log(rowHTML);
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";

        // genres
        rowHTML += "<td><ul>";
        for (let j = 0; j < resultData[i]["movie_genres"].length; j++) {
            rowHTML += "<li>" + resultData[i]["movie_genres"][j] + "</li>"
        }
        rowHTML += "</ul></td>"

        // stars
        rowHTML += "<td><ul>";
        for (let j = 0; j < resultData[i]["movie_stars"].length; j++) {
            rowHTML += "<li>" + resultData[i]["movie_stars"][j] + "</li>"
        }
        rowHTML += "</ul></td>"

        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
        rowHTML += "</tr>";

        movieTable.append(rowHTML);
    }
}

$.ajax({
   dataType: "json",
   method: "GET",
   url: "api/movielist",
    success: (resultData) => handleMovielistResult(resultData)
});