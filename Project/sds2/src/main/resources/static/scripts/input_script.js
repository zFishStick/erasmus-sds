
let selectedCity = null;

function normalizeText(s) {
  try {
    return s.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
  } catch (_) {
    return s.toLowerCase();
  }
}

window.addEventListener('DOMContentLoaded', () => {
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
      const country = it.country || '';
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

    const immediate = (function () {
      const label = value;
      if (labelMap.has(label)) return labelMap.get(label);
      const normLabel = normalizeText(label);
      for (const [k, v] of labelMap.entries()) {
        if (normalizeText(k) === normLabel) return v;
      }
      const cityOnly = label.split(',')[0].trim();
      const normCity = normalizeText(cityOnly);
      const byNameEq = lastItems.find(it => normalizeText(it.name || '') === normCity);
      if (byNameEq) return byNameEq;
      return lastItems.find(it => normalizeText(it.name || '').startsWith(normCity)) || null;
    })();
    if (immediate) {
      selectedCity = immediate;
      if (value.includes(',')) {
        return;
      }
    }

    debounceTimer = setTimeout(() => {
      const queryForAPI = value.includes(',') ? value.split(',')[0].trim() : value;
      if (queryForAPI.length < 2) { return ; }
      fetch(`/city/${encodeURIComponent(queryForAPI)}`)
        .then(res => res.json())
        .then(data => {
          const items = Array.isArray(data) ? data : [];          
          updateDatalist(items, value);

          const label = the_input.value.trim();
          const cityOnly = label.split(',')[0].trim();
          const normCity = normalizeText(cityOnly);

          const eq = items.find(it => normalizeText(it.name || '') === normCity);
          if (eq) {
            selectedCity = eq;
          } else if (items.length > 0) {
            selectedCity = items[0];
          }
        })
        .catch(err => {
          console.error("Error fetching location data:", err);
          while (datalist.firstChild) datalist.removeChild(datalist.firstChild);
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

    const cityOnly = label.split(',')[0].trim();
    const normCity = normalizeText(cityOnly);
    const byEq = lastItems.find(it => normalizeText(it.name || '') === normCity);
    if (byEq) return byEq;
    const byPrefix = lastItems.find(it => normalizeText(it.name || '').startsWith(normCity));
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
        const value = the_input.value.trim();
        if (value.length >= 2) {
          try {
            const res = await fetch(`/city/${encodeURIComponent(value)}`);
            const data = await res.json();
            const items = (data && data.data) ? data.data : [];
            selectedCity = items.length > 0 ? items[0] : null;
          } catch (e) {
            console.error("Error fetching city data:", e);
          }
        }
      }
    }

    if (!selectedCity) {
      console.error("No city selected or resolved.");
      return;
    }


    //fetchTravelInfo();
  });

  the_form.addEventListener('submit', function(event) {
    alert("Submitting form...");
      if (!selectedCity) {
          alert('Please select a valid city.');
          event.preventDefault();
          return;
      }

      document.getElementById('geo-latitude').value = selectedCity.latitude;
      document.getElementById('geo-longitude').value = selectedCity.longitude;
      document.getElementById('country-code').value = selectedCity.country;

      console.log("Info: " + selectedCity.name, selectedCity.latitude, selectedCity.longitude, selectedCity.country);
      the_form.submit()
  });


});

// the_form.addEventListener('submit', function(event) {
//     if (!selectedCity) {
//         alert('Please select a valid city.');
//         event.preventDefault();
//         return;
//     }

//     document.getElementById('geo-latitude').value = selectedCity.latitude;
//     document.getElementById('geo-longitude').value = selectedCity.longitude;
//     document.getElementById('country-code').value = selectedCity.country;

//     // Imposta l'action corretta sul form
//     the_form.action = `/pois/${encodeURIComponent(selectedCity.country)}/${encodeURIComponent(selectedCity.name)}`;
//     // submit naturale -> farà POST e poi redirect dal controller
// });


// function fetchTravelInfo() {
//   const startEl = document.getElementById('start-date');
//   const endEl = document.getElementById('end-date');
//   const checkInDate = startEl && startEl.value ? startEl.value : null;
//   const checkOutDate = endEl && endEl.value ? endEl.value : null;

//   fetch(`/pois/${encodeURIComponent(selectedCity.country)}/${encodeURIComponent(selectedCity.name)}`, {
//       method: 'POST',
//       headers: {'Content-Type': 'application/json'},
//       body: JSON.stringify({
//           city: selectedCity.name || '',
//           country: selectedCity.country || '',
//           geoCode: {
//               latitude: selectedCity.latitude,
//               longitude: selectedCity.longitude,
//           },
//           checkInDate: checkInDate,
//           checkOutDate: checkOutDate,
//       })
//   })
//   .then(res => {
//       if (!res.ok) {
//           throw new Error(`Server error: ${res.status}`);
//       }
//       return res.text();
//   })
//   .then(html => {
//       console.log("Received POIs page HTML.");
//       // document.documentElement.innerHTML = html;
//       // let path = `/pois/${encodeURIComponent(selectedCity.country)}/${encodeURIComponent(selectedCity.name)}`;
//       // window.history.pushState({}, '', path);
//   })
//   .catch(err => {
//       console.error('POIs fetch failed:', err);
//   });
// }

