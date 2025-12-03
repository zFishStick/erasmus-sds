let map;
let directionsService;
let directionsRenderer;
let placeAutocomplete;

let waypoints = [];
let waypointMarkers = [];

let origin;
let destination;

let originMarker = null;
let newOrigin = null;

document.getElementById("route-form").addEventListener("submit", (event) => {
    event.preventDefault();

    const intermediates = waypoints.filter(wp => wp !== destination);
    const travelMode = document.getElementById("travel-mode-select").value;
    const departureTime = document.getElementById("departure-time").value;
    const city = document.getElementById("city-input").value;

    if (!origin) {
        alert("Please select an origin point.");
        return;
    }

    const body = {
        origin: origin,
        destination: destination,
        intermediates: intermediates,
        travelMode: travelMode,
        departureTime: departureTime
    };
    fetch("/routes/create/" + encodeURIComponent(city), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
    .then(response => response.json())
    .then(routeDTO => {
        console.log("RouteDTO:", routeDTO);
        if (!routeDTO || !routeDTO.encodedPolyline) {
            console.error("encodedPolyline missing!");
            return;
        }
        drawRoute(routeDTO.encodedPolyline);
    })
    .catch(console.error);
});

function drawRoute(encodedPolyline) {
    const path = google.maps.geometry.encoding.decodePath(encodedPolyline);

    const routeLine = new google.maps.Polyline({
        path: path,
        geodesic: true,
        strokeColor: "#FF0000",
        strokeOpacity: 0.8,
        strokeWeight: 5
    });

    routeLine.setMap(map);
}



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
            lat: place.location.lat(),
            lng: place.location.lng()
        }

        if (originMarker) {
            originMarker.map = originMarker.setMap(null);
        }

        console.log("Origin set to: {" + newOrigin.name + ", " + newOrigin.address + "}");
        console.log("Origin set to: " + newOrigin.lat + ", " + newOrigin.lng);
        
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
        lat: lat,
        lng: lng
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
    await fillWaypointsArray();
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
            lat: lat,
            lng: lng
        });
    });

    destination = waypoints.length > 0 ? waypoints[0] : null;

    waypoints.forEach(element => {
        const marker = new google.maps.marker.AdvancedMarkerElement({
            map: map,
            position: { lat: element.lat, lng: element.lng },
            title: element.name,
            gmpClickable: true
        });
        waypointMarkers.push(marker);
        console.log(element);
    });

    console.log("Destination: " + waypoints[0].name);
}


document.getElementById("destination-select").addEventListener("change", () => {

    let oldDestination = destination;

    const selectedValue = document.getElementById("destination-select").value;
    destination = waypoints.find(wp => {
        return (wp.lat + ',' + wp.lng) === selectedValue;
    });

    waypoints = waypoints.filter(wp => {
        return !(wp.lat === destination.lat && wp.lng === destination.lng);
    });

    waypoints.push(oldDestination);

    console.log("New destination: " + destination.name);

});