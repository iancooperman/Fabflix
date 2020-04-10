



function handleMovielistResult(resultData) {
    console.log("This is getting call, okay?");
}

jQuery.ajax({
   dataType: "json",
   method: "GET",
   url: "api/movielist",
    success: (resultData) => handleMovielistResult(resultData)
});