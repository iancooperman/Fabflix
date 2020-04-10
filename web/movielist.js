



function handleMovielistResult(resultData) {
    console.log("This is getting called, yes?");

    let movieTable = $("#movie-table");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<td>" + resultData[i]["movie_title"] + " (" + resultData[i]["movie_year"] + ")" + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + "Don't care for now" + "</td>";
        rowHTML += "<td>" + "Don't care for now" + "</td>";
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