
let mapBoxKey;

fetch("/api-key")
  .then(res => res.json())
  .then(data => {
    mapBoxKey = data.key;
    initInputAutocomplete();
  })
  .catch(err => console.error("❌ Error loading API key:", err));

function initInputAutocomplete() {
  const autofillElement = document.getElementById('autofill');
  const the_input = document.getElementById('destination');
  const the_form = document.getElementById('planner-form');

  autofillElement.accessToken = mapBoxKey;
  autofillElement.options = {
    limit: 10,
    language: 'en',
    country: 'pl'
  };

  autofillElement.componentOptions = {
    flipCoordinates: true
  };

  autofillElement.interceptSearch = (val) => val?.length > 1 ? val : null;

  autofillElement.addEventListener('suggest', (event) => {
    console.log('Suggestions:', event.detail.suggestions);
  });

  autofillElement.addEventListener('retrieve', (event) => {
    console.log('Retrieved features:', event.detail);
  });

  the_input.addEventListener('retrieve', (event) => {
    console.log("Retrieved location:", event.detail);
  });

  the_form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const query = the_input.value.trim();
    if (!query) {
      alert("Please enter a location.");
      return;
    }

    try {
      const response = await fetch(
        `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(query)}.json?access_token=${mapBoxKey}&limit=1`
      );
      const data = await response.json();
      if (data.features?.length) {
        const coordinates = data.features[0].geometry.coordinates;
        fetchTravelInfo(coordinates);
      } else {
        console.log("No results found");
      }
    } catch (error) {
      console.error("Error fetching coordinates:", error);
    }
  });
}

function fetchTravelInfo(coordinates) {
    // const city = document.getElementById("city").value;
    // const startDate = document.getElementById("start_date").value;
    // const endDate = document.getElementById("end_date").value;

    fetch('/amadeus/pois', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            longitude: coordinates[0],
            latitude: coordinates[1],
            // city: city,
            // startDate: startDate,
            // endDate: endDate
        })
    })
    .then(res => res.json())
    .then(data => {
        showPointsOfInterest(data);
    })
    .catch(err => console.error("❌ Error fetching travel info:", err));
}

function showPointsOfInterest(data) {
  sessionStorage.setItem("poiData", JSON.stringify(data));
  // Change page to pointOfInterests.html or update the DOM to show POIs
  window.location.href = "/gui/pages/pointInterests.html";
  let list = document.getElementById("poi-list");
  for (let poi of data.pois) {
    let listItem = document.createElement("li");
    listItem.textContent = poi.name + " - " + poi.category;
    list.appendChild(listItem);
  }
}