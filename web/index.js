function handleResult(resultData) {
    let genreList = $("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genreId = resultData[i]["genreId"];
        let genreName = resultData[i]["genreName"];

        let parameterObject = movielistDefaultValues;
        parameterObject["genre"] = genreId;
        let serializedParameters = $.param(parameterObject);

        let href = "movielist.html?" + serializedParameters;
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

function createTextPattern(text) {
    if (text !== "") {
        return "%" + text + "%";
    }
    else {
        return text;
    }
}


function handleSubmission(eventObject) {
    console.log("Is this gettin called?");
    eventObject.preventDefault();

    let title = $("#title").val();
    let titlePattern = createTextPattern(title);

    let year = $("#year").val();

    if (year === "") {
        year = "0";
    }

    let directorPattern = createTextPattern($("#director").val());
    let starPattern = createTextPattern($("#star").val());

    let parameterObject = movielistDefaultValues;
    parameterObject["title"] = titlePattern;
    parameterObject["year"] = year;
    parameterObject["director"] = directorPattern;
    parameterObject["star"] = starPattern;

    let serializedParameters = $.param(parameterObject);

    let movielistURL = "movielist.html?" + serializedParameters;
    // let the redirection commence!
    window.location.href = movielistURL;


}

fillGenreList();
$("#search-form").submit(handleSubmission);
