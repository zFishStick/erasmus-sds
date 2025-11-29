
function initMap() {
    const lat =  parseFloat(document.getElementById('latitude').value);
    const lng = parseFloat(document.getElementById('longitude').value);
    const name = document.getElementById('place-name').textContent;

    let mapElement = document.getElementById('gmap');
    if (!mapElement) {
        mapElement = document.createElement('div');
        mapElement.id = 'gmap';
        mapElement.style.width = '100%';
        mapElement.style.height = '400px';
        document.body.appendChild(mapElement);
    }

    const map = new google.maps.Map(mapElement, {
        center: { lat, lng },
        zoom: 15,
        mapId: '695fdba28dce8975c124de1a'
    });

    // const marker = new google.maps.marker.AdvancedMarkerElement ({
    //     position: { lat, lng },
    //     map: map,
    //     gmpClickable: true
    // });

    // const markerEvent = marker.addListener('click', () => {
    //     showInfoWindow(name, lat, lng, map);
    // });

}

window.initMap = initMap;

function showInfoWindow(title, lat, lng, map) {

    let content = document.createElement('div');
    content.className = 'info-window-content';
    content.textContent = title;

    const infoWindow = new google.maps.InfoWindow({
        content: content,
        position: { lat, lng }
    });
    infoWindow.open(map);
}