// Attach this script to pages with a logout button
// (tag with id="logout_button")

function logout() {
    $.ajax(
        "api/logout", {
            method: "POST",
        }
    )

    location.reload();
    return false;
}

$("#logout_button").on("click", logout);