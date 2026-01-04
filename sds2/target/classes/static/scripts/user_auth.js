
document.addEventListener("DOMContentLoaded", function () {

    const userAuthLink = document.getElementById("user-auth");

    fetch("/user/status")
        .then(res => res.json())
        .then(data => {

            if (data.loggedIn) {
                userAuthLink.textContent = data.user.username;
                userAuthLink.href = "/user";

                const userIdInput = document.getElementById("user-id");
                if (userIdInput) {
                    userIdInput.value = data.user.id;
                }
                document.dispatchEvent(new CustomEvent("user-auth-ready", {
                    detail: { user: data.user }
                }));

            } else {
                userAuthLink.textContent = "Login";
                userAuthLink.href = "/user/login";
                
                document.dispatchEvent(new CustomEvent("user-auth-ready", {
                    detail: { user: null }
                }));
            }
        });
});

async function attemptLogin() {
    const form = document.querySelector("form");
    const formData = new FormData(form);
    const res = await fetch("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ email, password })
        });

        const data = await res.json();

        if (data.success) {
            window.location.href = "/user";
        } else {
            document.getElementById("message").textContent = data.errorMessage;
    }

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

