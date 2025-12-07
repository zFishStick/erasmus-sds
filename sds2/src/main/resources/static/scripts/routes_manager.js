let innerMap;
let marker;
let infoWindow;
let center;

let map;
let directionsService;
let directionsRenderer;
let waypoints = [];
let waypointMarkers = [];

let origin;
let destination;

let originMarker = null;
let newOrigin = null;

let lat,lng;

let routeSetted = false;

function getCurrentPosition() {
    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(pos => {
            lat = pos.coords.latitude;
            lng = pos.coords.longitude;
            center = { lat: lat, lng: lng };
            resolve();
        }, reject);
    });
}

function putMarkerAtLocation(lat, lng) {

    if (originMarker) {
        originMarker.setMap(null);
    }

    originMarker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: "Current Location",
        gmpClickable: true
    });

    origin = {
        name: "Current Location",
        address: "GPS Position",
        location: { lat, lng }
    };

}

function addWaypointMarker(lat, lng, title) {
    const marker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: title,
        gmpClickable: true
    });
    waypointMarkers.push(marker);
}

async function initMap() {
    
    await getCurrentPosition();

    await Promise.all([
        google.maps.importLibrary('marker'),
        google.maps.importLibrary('places'),
    ]);

    const mapElement = document.getElementById("map");
    const autocompleteElement = document.getElementById("map-autocomplete");
    map = mapElement.innerMap;

    map.setOptions({
        mapTypeControl: false,
        zoom: 13,
        center: center,
        mapId: "695fdba28dce8975c124de1a",
        mapTypeId: 'roadmap'
    });

    placeAutocomplete = autocompleteElement;

    map.addListener("bounds_changed", () => {
        placeAutocomplete.locationRestriction = map.getBounds();
    });

    placeAutocomplete.addEventListener('gmp-select', async ({ placePrediction }) => {
        const place = placePrediction.toPlace();
        await place.fetchFields({
            fields: ['displayName', 'formattedAddress', 'location'],
        });

        setOriginMarker(place.location.lat(), place.location.lng(), place.displayName);

        let content = document.createElement('div');
        let nameText = document.createElement('span');
        nameText.textContent = place.displayName;
        content.appendChild(nameText);
        content.appendChild(document.createElement('br'));
        let addressText = document.createElement('span');
        addressText.textContent = place.formattedAddress;
        content.appendChild(addressText);
        updateInfoWindow(content, place.location);
        marker.position = place.location;
    });


    marker = new google.maps.marker.AdvancedMarkerElement({
        map: innerMap,
    });
    
    infoWindow = new google.maps.InfoWindow({});

    fillWaypointsArray();
    initRouteButton();
}

function updateInfoWindow(content, center) {
    infoWindow.setContent(content);
    infoWindow.setPosition(center);
    infoWindow.open({
        map: innerMap,
        anchor: marker,
        shouldFocus: false,
    });
}

initMap();

function initRouteButton() {
    document.getElementById("route-form").addEventListener("submit", (event) => {
        event.preventDefault();
        computeRoute();
    });
}

function computeRoute() {
    if (!originMarker) {
        alert("Select an origin first!");
        return;
    }

    const originObj = origin.location;
    
    const destinationSelect = document.getElementById("destination-select");
    const destCoords = destinationSelect.value.split(',').map(parseFloat);
    const destinationObj = { lat: destCoords[0], lng: destCoords[1] };

    const waypointsArr = waypointMarkers.map(marker => ({
        location: marker.position,
        stopover: true
    }));

    const travelModeSelect = document.getElementById("travel-mode-select");

    const request = {
        origin: originObj,
        destination: destinationObj,
        waypoints: waypointsArr,
        travelMode: travelModeSelect.value
    };

    if (routeSetted) {
        directionsRenderer.setMap(null);
    }

    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
        suppressMarkers: false
    });

    directionsService.route(request, (result, status) => {
        if (status === "OK") {
            routeSetted = true;
            hideMarkers();
            directionsRenderer.setDirections(result);
        } else {
            console.error("Route error:", status);
            alert("Route could not be computed: " + status);
        }
    });
}

function hideMarkers() {
    if (originMarker) {
        originMarker.setMap(null);
    }

    waypointMarkers.forEach(marker => {
        marker.setMap(null);
    });
}


async function fillWaypointsArray() {
    const waypointForms = document.querySelectorAll(".waypoint-form");

    waypoints = [];
    waypointMarkers = [];

    waypointForms.forEach((form) => {
        const name = form.querySelector("input[name='name']").value;
        const address = form.querySelector("input[name='address']").value;
        const lat = parseFloat(form.querySelector("input[name='latitude']").value);
        const lng = parseFloat(form.querySelector("input[name='longitude']").value);

        const waypoint = {
            name: name,
            address: address,
            location: { lat: lat, lng: lng }
        };

        waypoints.push(waypoint);

        addWaypointMarker(lat, lng, name);

        console.log(waypoint);
    });

    destination = waypoints.length > 0 ? waypoints[0].location : null;

    if (destination) {
        console.log("Destination: " + waypoints[0].name);
    }
}

function addWaypointMarker(lat, lng, title) {
    const marker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: title,
        gmpClickable: true
    });
    waypointMarkers.push(marker);
}

function setOriginMarker(lat, lng, title) {
    if (originMarker) {
        originMarker.setMap(null);
    }

    originMarker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: title,
        gmpClickable: true
    });

    origin = {
        name: title,
        location: { lat, lng }
    };
}