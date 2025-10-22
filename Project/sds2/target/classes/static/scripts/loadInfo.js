
let pois = sessionStorage.getItem("poiData");

window.addEventListener("DOMContentLoaded", () => {
  if (pois) {
    pois = JSON.parse(pois);
  }

  const poiList = document.getElementById("poi-list");
  if (pois && pois.length) {
    pois.forEach(poi => {
      const li = document.createElement("li");
      li.textContent = poi.name;
      poiList.appendChild(li);
    });
  } else {
    poiList.textContent = "No points of interest found.";
  }
});