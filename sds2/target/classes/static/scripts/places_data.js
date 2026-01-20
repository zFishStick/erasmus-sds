const placeGroups = {
    food_and_drink: {
        label: "Food & Drink",
        description: city => `Discover the best places to eat and drink in ${city}, from restaurants to cafés and dessert spots.`,
        types: [
            "restaurant",
            "italian_restaurant",
            "bakery",
            "dessert_shop",
            "ice_cream_shop"
        ]
    },

    shopping: {
        label: "Shopping",
        description: city => `Explore the best shopping destinations in ${city}, from malls to local stores.`,
        types: [
            "shopping_mall",
            "clothing_store",
            "electronics_store",
            "department_store",
            "furniture_store",
            "home_goods_store",
            "grocery_store",
            "sporting_goods_store",
            "home_improvement_store",
            "book_store",
            "market"
        ]
    },

    culture_and_history: {
        label: "Culture & History",
        description: city => `Experience the cultural and historical heritage of ${city} through museums, landmarks, and monuments.`,
        types: [
            "museum",
            "historical_place",
            "historical_landmark",
            "monument",
            "church",
            "cultural_center",
            "performing_arts_theater"
        ]
    },

    nature_and_leisure: {
        label: "Nature & Leisure",
        description: city => `Relax and enjoy outdoor and leisure activities in ${city}.`,
        types: [
            "park",
            "botanical_garden",
            "zoo",
            "water_park",
            "plaza"
        ]
    },

    accommodation: {
        label: "Accommodation",
        description: city => `Find the best places to stay in ${city}, suitable for every budget and preference.`,
        types: [
            "hotel",
            "hostel",
            "bed_and_breakfast"
        ]
    },

    transport: {
        label: "Transport",
        description: city => `Move easily around ${city} using its transport infrastructure.`,
        types: [
            "train_station",
            "subway_station",
            "transit_station",
            "parking"
        ]
    },

    public_services: {
        label: "Public Services",
        description: city => `Access essential public and administrative services in ${city}.`,
        types: [
            "hospital",
            "city_hall",
            "university",
            "consultant",
            "insurance_agency"
        ]
    },

    events_and_sports: {
        label: "Events & Sports",
        description: city => `Attend events and enjoy sports activities in ${city}.`,
        types: [
            "event_venue",
            "stadium",
            "sports_club"
        ]
    },

    tourism: {
        label: "Tourism",
        description: city => `Discover the main tourist attractions that make ${city} unique.`,
        types: [
            "tourist_attraction"
        ]
    }
};

const typeToGroupMap = {};

for (const groupKey in placeGroups) {
    for (const type of placeGroups[groupKey].types) {
        typeToGroupMap[type] = groupKey;
    }
}
