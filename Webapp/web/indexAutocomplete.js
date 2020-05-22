$("#main_search_bar_input").autocomplete({
    lookup: [{value:"Toy Story", data: "ts"}, {value:"Toy Story 2", data: "ts2"}, {value:"Toy Story 3", data: "ts3"}],
    onSelect: function(suggestion) {
        alert(suggestion.data);
    }
})