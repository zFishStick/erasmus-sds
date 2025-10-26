
let access_token;

fetch("/amadeus/api/access-token")
  .then(data => {
    access_token = data;
    initInputAutocomplete();
  }).catch(err => console.error("❌ Error fetching Amadeus access token:", err));

let selectedCity = null;

function initInputAutocomplete() {
  const the_input = document.getElementById('destination');
  const the_form = document.getElementById('planner-form');
  let debounceTimer;

  the_input.addEventListener('input', function () {
    clearTimeout(debounceTimer);

    const value = the_input.value.trim();
    if (value.length < 3) return;

    debounceTimer = setTimeout(() => {
      console.log("🔎 Cercando città per:", value);

      fetch(`/amadeus/api/city/${encodeURIComponent(value)}`)
        .then(res => res.json())
        .then(data => {
          console.log("Auto-fill data:", data);
          if (data.data && data.data.length > 0) {
            selectedCity = data.data[0];
          } else {
            selectedCity = null;
          }
        })
        .catch(err => console.error("❌ Error fetching location data:", err));
    }, 500);
  });

  the_form.addEventListener('submit', function (event) {
    event.preventDefault();

    if (!selectedCity) {
      console.error("❌ Nessuna città selezionata!");
      return;
    }

    const coordinates = [
      selectedCity.geoCode.longitude,
      selectedCity.geoCode.latitude
    ];

    fetchTravelInfo(coordinates);
  });
}


function fetchTravelInfo(coordinates) {
  fetch(`/amadeus/pois/${selectedCity.name}`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
          city: selectedCity.name,
          longitude: coordinates[0],
          latitude: coordinates[1],
      })
  })
  .then(res => {
      if (!res.ok) {
          throw new Error(`Errore dal server: ${res.status}`);
      }
      return res.text();
  })
  .then(html => {
      document.documentElement.innerHTML = html;
      
      window.history.pushState({}, '', `/amadeus/pois/${encodeURIComponent(selectedCity.name)}`);
  })
  .catch(err => {
      console.error('Fetch fallita:', err);
  });
}