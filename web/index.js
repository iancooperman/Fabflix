function handleResult(resultData) {
    let genreList = $("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genreId = resultData[i]["genreId"];
        let genreName = resultData[i]["genreName"];

        let parameterObject = movielistDefaultValues();
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

    let parameterObject = movielistDefaultValues();
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

function createCharacterLI(character) {
    let parameters = movielistDefaultValues();
    parameters["title"] = character + "%";
    let href = "movielist.html?" + $.param(parameters);
    let html = "<li><a href='" + href + "'>" + character + "</a> </li>";
    return html;
}

function fillCharacterList() {
    let characterList = $("#character_list");

    for (let i = 48; i <= 57; i++) {
        let character = String.fromCharCode(i);
        let html = createCharacterLI(character);
        characterList.append(html);
    }

    for (let i = 65; i <= 90; i++) {
        let character = String.fromCharCode(i);
        let html = createCharacterLI(character);
        characterList.append(html);
    }

    let parameters = movielistDefaultValues();
    parameters["title"] = "*";
    let href = "movielist.html?" + $.param(parameters);
    let html = "<li><a href='" + href + "'>" + "*" + "</a></li>";

    characterList.append(html);





    $("#character_list").append();
}

fillCharacterList();
$("#search-form").submit(handleSubmission);
