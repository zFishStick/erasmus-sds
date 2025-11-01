
// Test filter
let filters = ['museum', 'park', 'historical', 'art'];


window.addEventListener('DOMContentLoaded', () => {
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