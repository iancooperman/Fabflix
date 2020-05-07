function movielistDefaultValues() {
    return {
        "title": "",
        "year": 0,
        "director": "",
        "star": "",
        "genre": 0,
        "limit": 10,
        "page": 1,
        "sortBy": "rating_desc_title_asc"
    };
}

function centsToDollars(cents) {
    // make sure we have the cents in string form
    let dollars = cents / 100;
    dollars = dollars.toLocaleString("en-US", {style: "currency", currency: "USD"});
    return dollars;
}