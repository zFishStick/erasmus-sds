
document.querySelectorAll(".remove-waypoint-form").forEach(form => {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const url = form.getAttribute("action");

        try {
            const res = await fetch(url, {
                method: "POST"
            });

            const message = await res.text();
            console.log(message);

        } catch (err) {
            console.error(err);
        }
    });
});