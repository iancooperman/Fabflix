

function handleMetadataResult(resultData) {
for (let i = 0; i < resultData.length; i++) {
        let html = "";
        html += "<table border='1' style='display: inline-block; margin-right: 2px; vertical-align: top'>";

        let tableObject = resultData[i];
        let tableName = tableObject["table_name"];
        let tableColumns = tableObject["table_columns"];

        html += "<thead><tr><th>" + tableName + "</th></tr></thead><tbody>"

        for (let j = 0; j < tableColumns.length; j++) {
            let columnObject = tableColumns[j];
            let columnName = columnObject["column_name"];
            let columnType = columnObject["column_type"];

            html += "<tr><td>" + columnName + ", " + columnType + "</td></tr>"
        }

        html += "</tbody></table>";

        // add compiled HTML to site
        $("#metadata").append(html);
    }
}

function handleAddStarResult(resultData) {
    let message = resultData["message"];
    $("#add_star_message").text(message);
}

function addStarSubmission(eventObject) {
    eventObject.preventDefault();

    let formParameters = $("#add_star_form").serialize();
    console.log(formParameters);

    $.ajax({
        dataType: "json",
        method: "get",
        url: "api/addStar?" + formParameters,
        success: (resultData) => handleAddStarResult(resultData)
    });
}

// dashboard init
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetadataResult(resultData)
});

// add submit listener to add star form
$("#add_star_form").on("submit", addStarSubmission);