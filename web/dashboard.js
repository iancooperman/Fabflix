

function handleMetadataResult(resultData) {
for (let i = 0; i < resultData.length; i++) {
        let html = "";
        html += "<table border='1'>";

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


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetadataResult(resultData)
});