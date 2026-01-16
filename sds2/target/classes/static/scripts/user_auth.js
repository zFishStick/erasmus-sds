
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

function initItineraryForm(itinerary_form, userAuthLink) {

    itinerary_form.addEventListener("submit", function(e) {
    e.preventDefault();
    checkIfUserExists(itinerary_form);
    });

    userAuthLink.addEventListener("click", function() {
        itinerary_form.dispatchEvent(new Event("submit"));
    });
}

const loginForm = document.getElementById("login-form");
if (loginForm) {
    loginForm.addEventListener("submit", function(e) {
        e.preventDefault();
        attemptLogin();
    });
}

async function attemptLogin() {
    const form = document.getElementById("login-form");
    const formData = new FormData(form);

    const params = new URLSearchParams();
    for (const [key, value] of formData.entries()) {
        params.append(key, value);
    }

    try {
        const res = await fetch("/user/login/ajax", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        const data = await res.json();

        if (data.success) {
            window.location.href = data.redirectUrl;
        } else {
            document.getElementById("message").textContent = mapMessageCodeToText(data.message);
        }
    } catch (err) {
        console.error(err);
        document.getElementById("message").textContent = "Server error. Please try again later.";
    }
}

function mapMessageCodeToText(code) {
    const messages = {
        "USER_NOT_FOUND": "Please log in or register to view your itinerary.",
        "INVALID_CREDENTIALS": "Invalid username or password. Please try again.",
        "SUCCESS": ""
    };
    return messages[code] || "An unknown error occurred.";
}
