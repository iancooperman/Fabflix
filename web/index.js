function handleResult(resultData) {
    let genreList = $("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genreId = resultData[i]["genreId"];
        let genreName = resultData[i]["genreName"];

        let href = "movielist.html?genre=" + genreId;
        let newHTML = "<li><a href='" + href + "'>" + genreName + "</a></li>";
        genreList.append(newHTML);
    }

}

function fillGenreList() {
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/genres",
        success: (resultData) => handleResult(resultData)
    });
}

function handleSubmission(eventObject) {
    console.log("Is this gettin called?");
    eventObject.preventDefault();

    let titlePattern = "%" + $("#title").val() + "%";
    let year = $("#year").val();

    if (year === "") {
        year = "0";
    }

    let directorPattern = "%" + $("#director").val() + "%";
    let starPattern = "%" + $("#star").val() + "%";


    let movielistURL = "movielist.html?title=" + titlePattern
        + "&year=" + year
        + "&director=" + directorPattern
        + "&star=" + starPattern;

    // let the redirection commence!
    window.location.href = movielistURL;


}

fillGenreList();
$("#search-form").submit(handleSubmission);
