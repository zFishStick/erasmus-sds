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

function setOriginFromCoordinates(lat, lng) {

    if (originMarker) {
        originMarker.setMap(null);
    }

    originMarker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: "Current position",
        gmpClickable: true
    });

    origin = {
        name: "Current position",
        address: "Detected via GPS",
        location: { lat, lng }
    };

    map.setCenter({ lat, lng });
    map.setZoom(14);
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

        setOriginMarker(place);

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
    document.getElementById("compute-route-btn").addEventListener("click", (event) => {
        event.preventDefault();
        computeRoute();
    });
}

function saveRoute() {
    const form = document.getElementById("route-form");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!origin) {
            alert("Select an origin first");
            return;
        }

        const destination = getSelectedDestination();
        const intermediates = getIntermediates(
            destination.latitude,
            destination.longitude
        );

        const data = {
            userId: document.getElementById("user-id").value,
            routeIdentifier: document.getElementById("route-identifier").value,
            city: document.getElementById("route-city").value,
            country: document.getElementById("route-country").value,
            origin: {
                name: origin.name,
                address: origin.address,
                latitude: origin.latitude,
                longitude: origin.longitude,
                destination: origin.destination,
                country: origin.country
            },
            destination: destination,
            intermediates: intermediates,
            travelMode: document.getElementById("travel-mode-select").value,
            departureTime: document.getElementById("departure-time").value
        };

        console.log("Saving route:", data);

        const response = await fetch(form.action, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        const result = await response.text();
        alert(result);
    });
}

saveRoute();

function getIntermediates(destinationLat, destinationLng) {
    return waypoints
        .filter(wp =>
            wp.location.lat !== destinationLat ||
            wp.location.lng !== destinationLng
        )
        .map(wp => ({
            name: wp.name,
            address: wp.address,
            latitude: wp.location.lat,
            longitude: wp.location.lng
        }));
}

function getSelectedDestination() {
    const select = document.getElementById("destination-select");
    const selectedOption = select.options[select.selectedIndex];

    const [lat, lng] = selectedOption.value.split(",").map(Number);

    return {
        name: selectedOption.textContent,
        address: "Waypoint destination",
        latitude: lat,
        longitude: lng
    };
}



function computeRoute() {
    if (!originMarker) {
        alert("Select an origin first!");
        return;
    }
    
    const destinationSelect = document.getElementById("destination-select");
    const destCoords = destinationSelect.value.split(',').map(parseFloat);
    const destinationObj = { lat: destCoords[0], lng: destCoords[1] };

    const waypointsArr = waypointMarkers.map(marker => ({
        location: marker.position,
        stopover: true
    }));

    const travelModeSelect = document.getElementById("travel-mode-select");

    const request = {
        origin: originMarker.position,
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

function setOriginMarker(place) {

    const lat = place.location.lat();
    const lng = place.location.lng();

    const city = document.getElementById("route-city").value;
    const country = document.getElementById("route-country").value;

    if (originMarker) {
        originMarker.setMap(null);
    }

    originMarker = new google.maps.marker.AdvancedMarkerElement({
        map: map,
        position: { lat, lng },
        title: place.displayName,
        gmpClickable: true
    });

    origin = {
        name: place.displayName,
        address: place.formattedAddress,
        latitude: lat,
        longitude: lng,
        destination: city,
        country: country
    };
}

const removeWaypointForm = document.getElementById("remove-waypoint-form");
if (removeWaypointForm) {
    removeWaypointForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        showConfirmationDialog();
    });
}

function showConfirmationDialog() {
    let confirmationBox = document.getElementById("confirmation-dialog");
    confirmationBox.style.display = "flex";

    document.getElementById("confirm-yes-btn").onclick = async function() {
        confirmationBox.style.display = "none";
        await submitRemoveForm();
    };

    document.getElementById("confirm-no-btn").onclick = function() {
        confirmationBox.style.display = "none";
    }
}

async function submitRemoveForm() {
    const form = document.getElementById("remove-waypoint-form");
    const url = form.getAttribute("action");
    try {
        const res = await fetch(url, {
            method: "POST"
        });
        const message = await res.text();
        console.log(message);
        window.location.reload();
    } catch (err) {
        console.error(err);
    }
}
