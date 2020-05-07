

let prices = [];


function handleResult(resultData) {
// iterate through the movies
    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i];
        let movieId = movie["movie_id"];
        let movieTitle = movie["movie_title"];
        let movieYear = movie["movie_year"];
        let movieQuantity = movie["movie_quantity"];
        let moviePrice = movie["movie_price"];

        // push the price in cents for later retrieval
        prices.push(moviePrice);

        // populate table
        let innerHTML = "<tr>";
        innerHTML += "<td>" + movieTitle + " (" + movieYear + ")" + "</td>";
        innerHTML += "<td><input class='quantity' type='number' min='0' value='" + movieQuantity + "'></td>";
        innerHTML += "<td><span class='price'>" + centsToDollars(moviePrice) + "</span></td>";
        innerHTML += "</tr>";

        $("#cart_table > tbody").append(innerHTML);
    }

    calculateSubtotal();
}

function calculateSubtotal() {
    let subtotal = 0;

    let i = -1;
    $("#cart_table > tbody > tr").each(function() {
        i++;
        // let $this = $(this);
        let quantity = $(this).find(".quantity").val();
        let price = prices[i];

        subtotal += quantity * price;
    });

    $("#subtotal").text(centsToDollars(subtotal));
}





$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cartInfo",
    success: (resultData) => handleResult(resultData)
});