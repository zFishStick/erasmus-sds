let city = "";

const placeTypes = {
    "shopping_mall": "Shopping Mall", 
    "cultural_center": "Cultural Center", 
    "hotel": "Hotel", 
    "consultant": "Consultant", 
    "university": "University", 
    "museum": "Museum", 
    "hospital": "Hospital", 
    "city_hall": "City Hall", 
    "dessert_shop": "Dessert Shop",
    "parking": "Parking",
    "tourist_attraction": "Tourist Attraction",
    "restaurant": "Restaurant",
    "train_station": "Train Station",
    "event_venue": "Event Venue",
    "park": "Park",
    "zoo": "Zoo",
    "botanical_garden": "Botanical Garden"
};

function getDescriptions(city) {
    return {
        "shopping_mall": `Looking to make some shopping? Check out the shopping malls in ${city} for a variety of stores and boutiques.`,
        "cultural_center": `Explore the rich culture of ${city} by visiting its cultural centers, where art and history come alive.`,
        "hotel": `Planning a stay in ${city}? Find the best hotels that offer comfort and convenience for your visit.`,
        "consultant": `Need expert advice in ${city}? Our consultants are here to help you with a range of services.`,
        "university": `Discover the top universities in ${city} that provide quality education and research opportunities.`,
        "museum": `Immerse yourself in the history and art of ${city} by visiting its renowned museums.`,
        "hospital": `Find the best healthcare facilities in ${city} with our list of top hospitals.`,
        "city_hall": `Learn more about the governance of ${city} by visiting its city hall.`,
        "dessert_shop": `Satisfy your sweet tooth at the finest dessert shops in ${city}, offering a variety of treats.`,
        "parking": `Find convenient parking options in ${city} to make your visit hassle-free.`,
        "tourist_attraction": `Discover the must-see tourist attractions in ${city} that showcase its unique charm and history.`,
        "restaurant": `Enjoy the best dining experiences in ${city} at a variety of restaurants offering diverse cuisines, or find the perfect spot for a quick bite.`,
        "train_station": `Travel conveniently with the main train stations in ${city}, connecting you to various destinations.`,
        "event_venue": `Attend exciting events at the top event venues in ${city}, perfect for concerts, conferences, and more.`,
        "park": `Relax and enjoy the natural beauty of ${city} by visiting its parks, ideal for picnics, walks, and outdoor activities.`,
        "zoo": `Visit the zoo in ${city} to see a variety of animals and enjoy a fun day out with family and friends.`,
        "botanical_garden": `Explore the botanical gardens in ${city} to discover a wide variety of plants and enjoy a peaceful environment.`
    };
}

document.addEventListener('DOMContentLoaded', () => {
    city = document.getElementById('city').value;
    const descriptions = getDescriptions(city);

    const places = Array.from(document.querySelectorAll('.place-item'));

    if (places.length === 0) return;

    const mainContainer = document.createElement('div');
    mainContainer.id = 'places-sections';

    const sections = {};

    for (const place of places) {
        const typeElement = place.querySelector('.place-type');
        const type = typeElement.textContent.trim();

        if (!sections[type]) {
            sections[type] = createDivSection(type, descriptions[type]);
            mainContainer.appendChild(sections[type]);
        }

        sections[type].appendChild(place);
    }

    const header = document.querySelector('.header-section');
    if (header) {
        header.insertAdjacentElement('afterend', mainContainer);
    }
});

  
function createDivSection(type, descriptionText) {
    const div = document.createElement('div');
    div.classList.add('place-description-section');
    div.id = type + '-description-section';

    const headerDiv = document.createElement('div');
    headerDiv.classList.add('place-description-header');
    div.appendChild(headerDiv);

    const header = document.createElement('h2');
    header.textContent = placeTypes[type] || type;
    headerDiv.appendChild(header);

    if (descriptionText) {
        const paragraph = document.createElement('p');
        paragraph.textContent = descriptionText;
        headerDiv.appendChild(paragraph);
    }

    return div;
}
