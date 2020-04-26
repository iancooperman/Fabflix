function handleResult(resultData) {
    console.log(resultData);

    let genreList = $("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genreId = resultData[i]["genreId"];
        let genreName = resultData[i]["genreName"];

        let href = "movielist.html?genre=" + genreId;
        let newHTML = "<li><a href='" + href + "'>" + genreName + "</a></li>";
        genreList.append(newHTML);
    }

}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/genres",
    success: (resultData) => handleResult(resultData)
});