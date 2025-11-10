
let selectedCity = null;

function normalizeText(s) {
  try {
    return s.normalize('NFD').replaceAll(/\p{Diacritic}/gu, '').toLowerCase();
  } catch (_) {
    return s.toLowerCase();
  }
}

globalThis.addEventListener('DOMContentLoaded', () => {
  const the_input = document.getElementById('destination');
  const the_form = document.getElementById('planner-form');
  const datalist = document.getElementById('city-suggestions');
  let debounceTimer;
  let lastItems = [];
  let labelMap = new Map();

  function updateDatalist(items, query) {
    while (datalist.firstChild) datalist.firstChild.remove();
    labelMap.clear();

    const q = normalizeText(query);
    const filtered = (items || []).filter(it => {
      const name = it?.name || '';
      return normalizeText(name).startsWith(q);
    }).slice(0, 8);

    for (const it of filtered) {
      const country = it.country || '';
      const label = country ? `${it.name}, ${country}` : it.name;
      const opt = document.createElement('option');
      opt.value = label;
      datalist.appendChild(opt);
      labelMap.set(label, it);
    }
    lastItems = filtered;
  }

  the_input.addEventListener('input', function () {
    clearTimeout(debounceTimer);

    const value = the_input.value.trim();
    if (value.length < 2) {
      while (datalist.firstChild) datalist.firstChild.remove();
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
          const items = data?.data ?? [];
          selectedCity = items.length > 0 ? items[0] : null;
        } catch (e) {
          console.error("Error fetching city data:", e);
        }
      }
    }
  }

  if (!selectedCity) {
    alert("Please select a valid city.");
    return;
  }

  document.getElementById('destination').value = selectedCity.name;
  document.getElementById('geo-latitude').value = selectedCity.latitude;
  document.getElementById('geo-longitude').value = selectedCity.longitude;
  document.getElementById('country-code').value = codeToCountryName(selectedCity.country);

  console.log(
    "Info:",
    selectedCity.name,
    selectedCity.latitude,
    selectedCity.longitude,
    selectedCity.country
  );

  the_form.submit();
});

});

function codeToCountryName(code) {
  const regionNames = new Intl.DisplayNames(['en'], { type: 'region' });
  return regionNames.of(code);
}