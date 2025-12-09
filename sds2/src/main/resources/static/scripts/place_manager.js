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
    "botanical_garden": "Botanical Garden",
    "church": "Church",
    "bed_and_breakfast": "Bed and Breakfast",
    "subway_station": "Subway Station",
    "bakery": "Bakery",
    "italian_restaurant": "Italian Restaurant",
    "performing_arts_theater": "Performing Arts Theater",
    "ice_cream_shop": "Ice Cream Shop",
    "historical_place": "Historical Place",
    "clothing_store": "Clothing Store",
    "market": "Market",
    "hostel": "Hostel"
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
        "botanical_garden": `Explore the botanical gardens in ${city} to discover a wide variety of plants and enjoy a peaceful environment.`,
        "church": `Experience the architectural beauty and spiritual ambiance of the churches in ${city}.`,
        "bed_and_breakfast": `Looking for a cozy stay in ${city}? Check out the charming bed and breakfast options available.`,
        "subway_station": `Navigate ${city} easily by using its subway stations, providing efficient transportation across the city.`,
        "bakery": `Indulge in freshly baked goods at the best bakeries in ${city}, offering a variety of breads, pastries, and cakes.`,
        "italian_restaurant": `Craving Italian cuisine? Discover the finest Italian restaurants in ${city} for an authentic dining experience.`,
        "performing_arts_theater": `Experience the vibrant performing arts scene in ${city} by visiting its top theaters for plays, musicals, and performances.`,
        "ice_cream_shop": `Cool off with delicious treats from the best ice cream shops in ${city}, offering a variety of flavors to satisfy your cravings.`,
        "historical_place": `Step back in time by exploring the historical places in ${city}, rich with stories and heritage.`,
        "clothing_store": `Update your wardrobe with the latest fashion trends at the top clothing stores in ${city}.`,
        "market": `Experience the local culture and flavors by visiting the vibrant markets in ${city}, where you can find fresh produce, crafts, and more.`,
        "hostel": `Traveling on a budget? Find affordable and comfortable hostels in ${city} for your stay.`
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
