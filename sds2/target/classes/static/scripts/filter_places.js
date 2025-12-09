
// Test filter
let filters = ['museum', 'park', 'historical', 'art'];


window.addEventListener('DOMContentLoaded', () => {
    alert("Latitude and Longitude: " + document.getElementById("latitude").value + ", " + document.getElementById("longitude").value);
    removeNotMatchingActivities();
});

function removeNotMatchingActivities() {
    const activities = document.querySelectorAll('ul');
    activities.forEach(activity => {
        const textContent = activity.textContent.toLowerCase();
        const matchesFilter = filters.some(filter => textContent.includes(filter.toLowerCase()));
        if (!matchesFilter) {
            activity.remove();
        }
    });
}

