
document.addEventListener("DOMContentLoaded", function() {

    const itinerary_form = document.getElementById("itinerary-form");
    let userAuthLink = document.getElementById("user-auth");
    const loginBtn = document.getElementById("login-btn");
        
    fetch("/user/status")
        .then(res => res.json())
        .then(data => {
            if (data.loggedIn) {
                userAuthLink.textContent = data.user.username;
                userAuthLink.href = "/user/" + data.user.id;
                document.getElementById("user-id").value = data.user.id;
            } else {
                userAuthLink.textContent = "Login";
                userAuthLink.href = "/user/login";
            }
        });

    if (itinerary_form) {
        initItineraryForm(itinerary_form, userAuthLink);
    }

    if (loginBtn) {
        loginBtn.addEventListener("click", function() {
        attemptLogin();
    });
    }

});

function attemptLogin() {
    const form = document.querySelector("form");
    const formData = new FormData(form);
    fetch("/user/login", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            window.location.href = data.redirectUrl;
        } else {
            document.getElementById("message").textContent = data.errorMessage;
        }
    });
}

function initItineraryForm(itinerary_form, userAuthLink) {

    itinerary_form.addEventListener("submit", function(e) {
    e.preventDefault();
    checkIfUserExists(itinerary_form);
    });

    userAuthLink.addEventListener("click", function() {
        itinerary_form.dispatchEvent(new Event("submit"));
    });


}

function checkIfUserExists(form) {

    const formData = new FormData(form);

    fetch("/user/login", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            window.location.href = data.redirectUrl;
        } else {
            document.getElementById("message").textContent = mapMessageCodeToText(data.message);
        }
    });
}

function mapMessageCodeToText(code) {
    const messages = {
        "USER_NOT_FOUND": "Please log in or register to view your itinerary.",
        "INVALID_CREDENTIALS": "Invalid username or password. Please try again."
    };
    return messages[code] || "An unknown error occurred.";
}

