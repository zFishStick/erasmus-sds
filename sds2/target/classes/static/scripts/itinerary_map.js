async function initItineraryMap(waypoints) {
    if (!waypoints || waypoints.length < 2) return;

    const origin = {
        lat: waypoints[0].location.latitude,
        lng: waypoints[0].location.longitude
    };

    const destination = {
        lat: waypoints[waypoints.length - 1].location.latitude,
        lng: waypoints[waypoints.length - 1].location.longitude
    };

    const center = origin;

    await initBaseMap(center);

    const intermediates = waypoints.slice(1, -1).map(wp => ({
        lat: wp.location.latitude,
        lng: wp.location.longitude
    }));

    computeRoute(map, origin, destination, intermediates);
}
