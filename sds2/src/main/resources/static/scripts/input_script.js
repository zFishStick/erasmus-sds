/* -------------------------------------------------------------------------- */
/* INITIALIZATION                                                             */
/* -------------------------------------------------------------------------- */

const hiddenInputs = [
  { id: "geo-latitude",  name: "latitude" },
  { id: "geo-longitude", name: "longitude" },
  { id: "iata-code",  name: "iataCode" },
  { id: "country-code",  name: "countryCode" }
];

function createHiddenInput(form) {
    hiddenInputs.forEach(id => {
      const input = document.createElement("input");
      input.type = "hidden";
      input.name = id.name;
      input.id = id.id;
      form.appendChild(input);
    });
  }

let selectedCity = null;

function normalizeText(s) {
  try {
    return s.normalize("NFD").replace(/\p{Diacritic}/gu, "").toLowerCase();
  } catch (_) {
    return s.toLowerCase();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const input = document.getElementById("destination");
  const form = document.getElementById("planner-form");
  const datalist = document.getElementById("city-suggestions");

  createHiddenInput(form);

  let debounceTimer;
  let lastItems = [];
  let labelMap = new Map();

  function clearDatalist() {
    datalist.innerHTML = "";
    labelMap.clear();
  }

  function matchCityFromText(text) {
    if (!text) return null;

    const trimmed = text.trim();
    const normLabel = normalizeText(trimmed);

    if (labelMap.has(trimmed)) return labelMap.get(trimmed);

    for (const [k, v] of labelMap.entries()) {
      if (normalizeText(k) === normLabel) return v;
    }

    const cityOnly = trimmed.split(",")[0].trim();
    const normCity = normalizeText(cityOnly);

    return (
      lastItems.find(it => normalizeText(it.name) === normCity) ||
      lastItems.find(it => normalizeText(it.name).startsWith(normCity)) ||
      null
    );
  }

  function updateDatalist(items, query) {
    clearDatalist();

    const q = normalizeText(query);

    const filtered = items
      .filter(it => normalizeText(it.name).startsWith(q))
      .slice(0, 8);

    filtered.forEach(it => {
      const label = it.country ? `${it.name}, ${it.country}` : it.name;
      const opt = document.createElement("option");
      opt.value = label;
      datalist.appendChild(opt);
      labelMap.set(label, it);
    });

    lastItems = filtered;
  }

  async function fetchCities(query) {
    try {
      const res = await fetch(`/city/${encodeURIComponent(query)}`);

      if (!res.ok) {
        console.error("Server returned error:", res.status);
        return [];
      }

      const data = await res.json();
      console.log("Data: " + JSON.stringify(data));
      
      return Array.isArray(data) ? data : [];
    } catch (err) {
      console.error("Error fetching location data:", err);
      return [];
    }
  }

  input.addEventListener("input", () => {
    clearTimeout(debounceTimer);

    const value = input.value.trim();
    if (value.length < 2) {
      clearDatalist();
      selectedCity = null;
      return;
    }

      const localMatch = matchCityFromText(value);
    if (localMatch) {
      selectedCity = localMatch;
      if (value.includes(",")) return;
    }

    debounceTimer = setTimeout(async () => {
      const queryForAPI = value.split(",")[0].trim();
      if (queryForAPI.length < 2) return;

      const items = await fetchCities(queryForAPI);
      updateDatalist(items, value);

      const cityOnly = value.split(",")[0].trim();
      const normCity = normalizeText(cityOnly);

      selectedCity =
        items.find(it => normalizeText(it.name) === normCity) ||
        items[0] ||
        null;
    }, 300);
  });

  input.addEventListener("change", () => {
    const match = matchCityFromText(input.value);
    if (match) selectedCity = match;
  });

  form.addEventListener("submit", async event => {
    event.preventDefault();

    if (!selectedCity) {
      selectedCity = matchCityFromText(input.value);

      if (!selectedCity) {
        const value = input.value.trim();

        if (value.length >= 2) {
          const items = await fetchCities(value);
          selectedCity = items[0] || null;
        }
      }
    }

    if (!selectedCity) {
      alert("Please select a valid city.");
      return;
    }

    document.getElementById("destination").value = selectedCity.name;

    assignValuesToHiddenInputs(selectedCity, hiddenInputs);

    form.submit();
  });

});

function codeToCountryName(code) {
  const regionNames = new Intl.DisplayNames(["en"], { type: "region" });
  return regionNames.of(code);
}

function assignValuesToHiddenInputs(city, hiddenInputs) {
  hiddenInputs.forEach(element => {
    switch (element.id) {
      case "geo-latitude":
        document.getElementById(element.id).value = city.latitude;
        break;
      case "geo-longitude":
        document.getElementById(element.id).value = city.longitude;
        break;
      case "iata-code":
        document.getElementById(element.id).value = city.iataCode;
        break;
      case "country-code":
        document.getElementById(element.id).value = codeToCountryName(city.country);
        break;
    }
  });
}
