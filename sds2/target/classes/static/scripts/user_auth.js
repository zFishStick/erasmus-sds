
document.addEventListener("DOMContentLoaded", function() {

    const form = document.querySelector("form");
    let userAuthLink = document.getElementById("user-auth");
    const loginBtn = document.getElementById("login-btn");
    
    fetch("/user/status")
        .then(res => res.json())
        .then(data => {
            if (data.loggedIn) {
                userAuthLink.textContent = data.user.username;
                userAuthLink.href = "/user/" + data.user.id;
            } else {
                userAuthLink.textContent = "Login";
                userAuthLink.href = "/user/login";
            }
        });

    form.addEventListener("submit", function(e) {
        e.preventDefault();
        checkIfUserExists(form);
    });

        
    loginBtn.addEventListener("click", function() {
        form.dispatchEvent(new Event("submit"));
    });

});

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
            document.getElementById("message").textContent = data.errorMessage;
        }
    });
}
