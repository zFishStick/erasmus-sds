// Main UI behavior for the Travel Planner
// - Validates dates (end >= start)
// - Renders a local preview of the form data
// - Stub for future API integration (handled by Java backend)

(function () {
  const $ = (sel, root = document) => root.querySelector(sel);
  const form = $("#planner-form");
  const preview = $("#preview");
  const destination = $("#destination");
  const start = $("#start-date");
  const end = $("#end-date");

  function setTodayMinDates() {
    const today = new Date();
    const pad = (n) => `${n}`.padStart(2, "0");
    const y = today.getFullYear();
    const m = pad(today.getMonth() + 1);
    const d = pad(today.getDate());
    const iso = `${y}-${m}-${d}`;
    start.min = iso;
    end.min = iso;
  }

  function validateDates() {
    if (!start.value || !end.value) return true;
    const s = new Date(start.value);
    const e = new Date(end.value);
    const ok = e >= s;
    end.setCustomValidity(ok ? "" : "End date must be after start date");
    return ok;
  }

  function renderPreview(data) {
    preview.hidden = false;
    const set = (key, val) => {
      const el = document.querySelector(`[data-preview="${key}"]`);
      if (el) el.textContent = val || "—";
    };
    set("destination", data.destination);
    set("startDate", data.startDate);
    set("endDate", data.endDate);
  }

  form?.addEventListener("submit", (e) => {
    e.preventDefault();
    if (!form.reportValidity() || !validateDates()) return;

    const payload = {
      destination: destination.value.trim(),
      startDate: start.value,
      endDate: end.value,
    };

    // Local preview for now; integration with backend will replace this.
    renderPreview(payload);
  });

  start?.addEventListener("change", validateDates);
  end?.addEventListener("change", validateDates);

  setTodayMinDates();
})();

