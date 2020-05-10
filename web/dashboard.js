

function handleResult(resultData) {
    console.log(resultData);

    for (let i = 0; i < resultData.length; i++) {
        let html = "";
        html += "<table border='1'>";

        let tableObject = resultData[i];
        let tableName = tableObject["table_name"];
        let tableColumns = tableObject["table_columns"];

        html += "<thead><tr><th>" + tableName + "</th></tr></thead>"

        for (let j = 0; j < tableColumns.length; j++) {
            let columnObject = tableColumns[j];
            let columnName = columnObject["column_name"];
            let columnType = columnObject["column_type"];
        }

        html += "</table>";

        // add compiled HTML to site
        $("#metadata").append(html);
    }


}


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleResult(resultData)
});