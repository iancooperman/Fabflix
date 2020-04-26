function handleResult(resultData) {
    let genreList = $("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let newHTML = "<li>" + resultData[i]["name"] + "</li>";
        genreList.append(newHTML)
    }

}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/browseByGenre",
    success: (resultData) => handleResult(resultData)
});