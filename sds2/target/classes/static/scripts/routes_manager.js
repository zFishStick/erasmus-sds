let map;
let directionsService;
let directionsRenderer;
let placeAutocomplete;
let waypoints = [];

let origin;
let destination;

let originMarker = null;
let newOrigin = null;

async function initAutocomplete() {
    let div = document.getElementById("autocomplete-div");

    placeAutocomplete = new google.maps.places.PlaceAutocompleteElement({});
    div.appendChild(placeAutocomplete);

    const selectedPlaceTitle = document.createElement('p');
    selectedPlaceTitle.textContent = '';
    document.body.appendChild(selectedPlaceTitle);
    const selectedPlaceInfo = document.createElement('pre');
    selectedPlaceInfo.textContent = '';
    document.body.appendChild(selectedPlaceInfo);
    placeAutocomplete.addEventListener('gmp-select', async ({ placePrediction }) => {
        const place = placePrediction.toPlace();
        await place.fetchFields({ fields: ['displayName', 'formattedAddress', 'location'] });
        selectedPlaceTitle.textContent = 'Selected Place:';
        selectedPlaceInfo.textContent = JSON.stringify(place.toJSON(), /* replacer */ null, /* space */ 2);

        newOrigin = {
            name: place.displayName,
            address: place.formattedAddress,
            location: place.location.toJSON()
        }

        if (originMarker) {
            originMarker.map = originMarker.setMap(null);
        }

        console.log("Origin set to: {" + newOrigin.name + ", " + newOrigin.address + "}");
        console.log("Origin set to: " + newOrigin.location.lat + ", " + newOrigin.location.lng);
        
        originMarker = new google.maps.marker.AdvancedMarkerElement({
                map: map,
                position: place.location,
                title: place.displayName,
                gmpClickable: true
        });

        origin = newOrigin;

        originMarker.addListener('gmp-click', () => {
            const infoWindow = new google.maps.InfoWindow();
            infoWindow.setContent(
                `<div><strong>${place.displayName}</strong><br>` +
                `Address: ${place.formattedAddress}</div>`+
                `Coordinates: (${place.location.lat()}, ${place.location.lng()})`
            );
            infoWindow.setPosition(place.location);
            infoWindow.open(map, marker);
        });
    });    

    document.getElementById("use-location-btn").addEventListener("click", () => {
        navigator.geolocation.getCurrentPosition(pos => {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;
            putMarkerAtLocation(lat, lng);
        });
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

async function initMap() {
    let lat,lon

    navigator.geolocation.getCurrentPosition(pos => {
        lat = pos.coords.latitude;
        lon = pos.coords.longitude;
        map.setCenter({ lat: lat, lng: lon })
    }),

    map = new google.maps.Map(document.getElementById("map"), {
        zoom: 12,
        mapId: '695fdba28dce8975c124de1a'
    });
    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer({ map: map, suppressMarkers: false });
    await initAutocomplete();
    initRouteButton();
    await fillWaypointsArray();
}

function initRouteButton() {
    document.getElementById("compute-route-btn").addEventListener("click", () => {
        computeRoute();
    });
}

function computeRoute() {
    const origin = placeAutocomplete.inputElement.value;
    const destinationSelect = document.getElementById("destination-select");
    const destination = destinationSelect ? destinationSelect.value : origin;
    const waypointForms = document.querySelectorAll(".waypoint-form");
    const waypoints = Array.from(waypointForms).map(f => ({
        location: {
            lat: parseFloat(f.querySelector("input[name='latitude']").value),
            lng: parseFloat(f.querySelector("input[name='longitude']").value)
        },
        stopover: true
    }));
    const request = {
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        travelMode: google.maps.TravelMode.DRIVING
    };
    directionsService.route(request, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
            directionsRenderer.setDirections(result);
        } else {
            console.error("Route error:", status);
            alert("Route could not be computed");
        }
    });
}

async function fillWaypointsArray() {
    const waypointForms = document.querySelectorAll(".waypoint-form");

    waypointForms.forEach((form) => {
        const name = form.querySelector("input[name='name']").value;
        const address = form.querySelector("input[name='address']").value;
        const lat = parseFloat(form.querySelector("input[name='latitude']").value);
        const lng = parseFloat(form.querySelector("input[name='longitude']").value);
        waypoints.push({
            name: name,
            address: address,
            location: { lat: lat, lng: lng }
        });
    });

    destination = waypoints.length > 0 ? waypoints[0].location : null;

    waypoints.forEach(element => {
        putMarkerAtLocation(element.location.lat, element.location.lng);
        console.log(element);
    });

    console.log("Destination: " + waypoints[0].name);
    
}

document.getElementById("destination-select").addEventListener("change", () => {
    const selectedValue = document.getElementById("destination-select").value;
    destination = selectedValue;

    console.log("New destination: " + destination.name);

});