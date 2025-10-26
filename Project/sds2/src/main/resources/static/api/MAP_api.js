
let access_token;

fetch("/amadeus/api/access-token")
  .then(res => res.text())
  .then(token => {
    access_token = token;
    initInputAutocomplete();
  })
  .catch(err => console.error("Error fetching Amadeus access token:", err));

let selectedCity = null;

function normalizeText(s) {
  try {
    return s.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
  } catch (_) {
    return s.toLowerCase();
  }
}

function initInputAutocomplete() {
  const the_input = document.getElementById('destination');
  const the_form = document.getElementById('planner-form');
  const datalist = document.getElementById('city-suggestions');
  let debounceTimer;
  let lastItems = [];
  let labelMap = new Map();

  function updateDatalist(items, query) {
    while (datalist.firstChild) datalist.removeChild(datalist.firstChild);
    labelMap.clear();

    const q = normalizeText(query);
    const filtered = (items || []).filter(it => {
      const name = it && it.name ? it.name : '';
      return normalizeText(name).startsWith(q);
    }).slice(0, 8);

    filtered.forEach(it => {
      const country = (it.address && (it.address.countryName || it.address.countryCode)) || '';
      const label = country ? `${it.name}, ${country}` : it.name;
      const opt = document.createElement('option');
      opt.value = label;
      datalist.appendChild(opt);
      labelMap.set(label, it);
    });
    lastItems = filtered;
  }

  the_input.addEventListener('input', function () {
    clearTimeout(debounceTimer);

    const value = the_input.value.trim();
    if (value.length < 2) {
      while (datalist.firstChild) datalist.removeChild(datalist.firstChild);
      selectedCity = null;
      return;
    }

    debounceTimer = setTimeout(() => {
      fetch(`/amadeus/api/city/${encodeURIComponent(value)}`)
        .then(res => res.json())
        .then(data => {
          const items = (data && data.data) ? data.data : [];
          updateDatalist(items, value);
          selectedCity = items.length > 0 ? items[0] : null;
        })
        .catch(err => {
          console.error("Error fetching location data:", err);
          while (datalist.firstChild) datalist.removeChild(datalist.firstChild);
          selectedCity = null;
        });
    }, 300);
  });

  function resolveFromInputValue() {
    const label = the_input.value.trim();
    if (!label) return null;
    if (labelMap.has(label)) return labelMap.get(label);

    // try case/diacritics-insensitive label match
    const normLabel = normalizeText(label);
    for (const [k, v] of labelMap.entries()) {
      if (normalizeText(k) === normLabel) return v;
    }

    // fallback: pick first lastItems whose name starts with the typed prefix
    const byPrefix = lastItems.find(it => normalizeText(it.name || '').startsWith(normLabel));
    return byPrefix || null;
  }

  the_input.addEventListener('change', function () {
    const match = resolveFromInputValue();
    if (match) selectedCity = match;
  });

  the_form.addEventListener('submit', async function (event) {
    event.preventDefault();

    if (!selectedCity) {
      const match = resolveFromInputValue();
      if (match) {
        selectedCity = match;
      } else {
        // final attempt: fetch with current input and take first result
        const value = the_input.value.trim();
        if (value.length >= 2) {
          try {
            const res = await fetch(`/amadeus/api/city/${encodeURIComponent(value)}`);
            const data = await res.json();
            const items = (data && data.data) ? data.data : [];
            selectedCity = items.length > 0 ? items[0] : null;
          } catch (e) {
            console.error('Erreur lors de la récupération finale:', e);
          }
        }
      }
    }

    if (!selectedCity) {
      console.error("Aucune ville sélectionnée ou trouvée.");
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
          throw new Error(`Erreur serveur: ${res.status}`);
      }
      return res.text();
  })
  .then(html => {
      document.documentElement.innerHTML = html;
      window.history.pushState({}, '', `/amadeus/pois/${encodeURIComponent(selectedCity.name)}`);
  })
  .catch(err => {
      console.error('Fetch des POIs échouée:', err);
  });
}

