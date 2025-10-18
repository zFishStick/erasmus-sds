//You should get your API key at https://dev.opentripmap.org
const apiKey = "5ae2e3f221c38a28845f05b6ddcb8b81a32325e88df4e7ebdec0f106";

function apiGet(method, query) {
  return new Promise(function(resolve, reject) {
    var otmAPI =
      "https://api.opentripmap.com/0.1/en/places/" +
      method +
      "?apikey=" +
      apiKey;
    if (query !== undefined) {
      otmAPI += "&" + query;
    }
    fetch(otmAPI)
      .then(response => response.json())
      .then(data => resolve(data))
      .catch(function(err) {
        console.log("Fetch Error :-S", err);
      });
  });
}