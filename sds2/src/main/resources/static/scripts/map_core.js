let map;

async function initBaseMap(center) {
    await google.maps.importLibrary('marker');

    const mapElement = document.getElementById("map");
    map = mapElement.innerMap;

    map.setOptions({
        zoom: 13,
        center: center,
        mapTypeControl: false,
        mapId: "695fdba28dce8975c124de1a",
    });

    return map;
}

function addMarker(lat, lng, title) {
    return new google.maps.marker.AdvancedMarkerElement({
        map,
        position: { lat, lng },
        title,
        gmpClickable: true
    });
}
