function setupSearchBox() {
    // add an event listener to the search bar
    let searchBar = $("#main_search_bar");
    searchBar.on("submit", searchBarSubmit);
}

function searchBarSubmit(eventObject) {
    eventObject.preventDefault();

    let inputText = $("#main_search_bar_input").val();
    let url = "movielist.html?" + $.param({"q": inputText});

    // redirect to movielist with proper parameters
    window.location.href = url;
}

setupSearchBox();