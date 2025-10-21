
let mapBoxKey;
fetch("/api-key")
  .then(res => res.json())
  .then(data => {
    mapBoxKey = data.key;
    initMapboxSearch();
  })
  .catch(err => console.error("❌ Error loading API key:", err));

function initMapboxSearch() {
      const script = document.getElementById('search-js');

      if (!script) {
        return;
      } else {
        const searchBoxElement = new MapboxSearchBox()
        searchBoxElement.id = "city"
        searchBoxElement.placeholder = "Enter a city to travel to"
        searchBoxElement.accessToken = mapBoxKey;
        searchBoxElement.classList.add('custom-search-box');
        searchBoxElement.options = {
          language: 'en',
          limit: 5,
        }
        document.getElementById("search_form").appendChild(searchBoxElement);

        const startDateElement = document.createElement('input');
        startDateElement.type = 'date';
        startDateElement.id = 'start_date';
        startDateElement.required = true;
        document.getElementById("search_form").appendChild(startDateElement);

        const endDateElement = document.createElement('input');
        endDateElement.type = 'date';
        endDateElement.id = 'end_date';
        endDateElement.required = true;
        document.getElementById("search_form").appendChild(endDateElement);

        const submitButton = document.createElement('button');
        submitButton.type = 'submit';
        submitButton.textContent = 'Search';
        document.getElementById("search_form").appendChild(submitButton);
        submitButton.onclick = function(event) {
          event.preventDefault();
          // if (!searchBoxElement.value) {
          //   alert("Please enter a city.");
          //   return false;
          // }
          fetchTravelInfo();
        }

      }
}

function fetchTravelInfo() {
    const city = document.getElementById("city").value;

    console.log("Coordinates lat: " + city.coordinates.latitude);
    console.log("Coordinates lon: " + city.coordinates.longitude);


    const startDate = document.getElementById("start_date").value;
    const endDate = document.getElementById("end_date").value;

    fetch('/travel-info', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            city: city,
            start_date: startDate,
            end_date: endDate
        })
    })
    .then(res => res.json())
    .then(data => {
        console.log("Travel info:", data);
        // TODO: show place in the map
    })
    .catch(err => console.error("❌ Error fetching travel info:", err));
}