let movieIds = [];
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

        // push movieIds for later retrieval
        movieIds.push(movieId);

        // push the price in cents for later retrieval
        prices.push(moviePrice);

        // populate table
        let innerHTML = "<tr>";
        innerHTML += "<td>" + movieTitle + " (" + movieYear + ")" + "</td>";
        innerHTML += "<td><input class='quantity' type='number' min='1' value='" + movieQuantity + "'></td>";
        innerHTML += "<td><span class='price'>" + centsToDollars(moviePrice) + "</span></td>";
        innerHTML += "<td><button class='delete_button'>Delete</button></td>"
        innerHTML += "</tr>";

        $("#cart_table > tbody").append(innerHTML);
    }

    // give quantity inputs an oninput event
    $(".quantity").on("input", calculateSubtotal);

    // give delete buttons an onclick event
    let i = -1;
    $(".delete_button").each(function() {
        i++;
        $(this).on("click", function() {deleteMovie($(this), i);} );
        console.log($(this));
    });

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


function deleteMovie(deleteButton, nthButton) {
    // delete movie from cart table
    console.log(nthButton);
    deleteButton.parents("tr").remove();

    // remove references to row info in arrays
    movieIds.splice(nthButton, 1);
    prices.splice(nthButton, 1);

    calculateSubtotal();

    // delete movie from session



}





$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cartInfo",
    success: (resultData) => handleResult(resultData)
});