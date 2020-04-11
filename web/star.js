function getUrlParam(param) {
    let searchParams = new URLSearchParams(window.location.search)
    return searchParams.get(param);
}


function handleStarResult(resultData) {
    console.log(resultData);
}


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/star?id=" + getUrlParam("id"),
    success: (resultData) => handleStarResult(resultData)
});/