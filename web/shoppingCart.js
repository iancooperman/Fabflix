




function handleResult(resultData) {
    console.log(resultData);
    // iterate through the movies
    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i];
        let movieId = movie["movie_id"];
        let movieTitle = movie["movie_title"];
        let movieYear = movie["movie_year"];
        let movieQuantity = movie["movie_quantity"];
        let moviePrice = movie["movie_price"];

        // populate table
        let innerHTML = "<tr>";
        innerHTML += "<td>" + movieTitle + " (" + movieYear + ")" + "</td>";
        innerHTML += "<td><input type='number' min='0' value='" + movieQuantity + "'></td>";
        innerHTML += "<td><span class='price'></span></td>";
        innerHTML += "</tr>";


        $("#cart_table").append(innerHTML);
    }
}





$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cartInfo",
    success: (resultData) => handleResult(resultData)
});