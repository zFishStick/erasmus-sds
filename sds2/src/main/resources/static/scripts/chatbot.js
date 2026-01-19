// AI itinerary helper for the index page.
(function () {
  const $ = (sel, root = document) => root.querySelector(sel);

  const destination = $("#destination");
  const startDate = $("#start-date");
  const endDate = $("#end-date");
  const promptInput = $("#ai-prompt");
  const generateButton = $("#ai-generate");
  const viewItinerary = $("#ai-view-itinerary");
  const status = $("#ai-status");
  const result = $("#ai-result");
  const output = $("#ai-output");
  const feasibility = $("#ai-feasibility");
  const activitiesContainer = $("#ai-activities");

  let userId = null;

  document.addEventListener("user-auth-ready", (e) => {
    const user = e.detail.user;
    userId = user ? user.id : null;
  });

  function getDestinationValue() {
    const value = destination?.value.trim() || "";
    const commaIndex = value.indexOf(",");
    return commaIndex !== -1 ? value.substring(0, commaIndex).trim() : value;
  }

  function getCountryValue() {
    return document.getElementById("country-code")?.value || "";
  }

  function readNumber(inputId) {
    const value = Number(document.getElementById(inputId)?.value);
    return Number.isFinite(value) ? value : null;
  }

  function readFilters() {
    return Array.from(document.querySelectorAll("input[name='filter']:checked"))
      .map((input) => input.value);
  }

  function setStatus(message, isError) {
    if (!status) return;
    status.hidden = !message;
    status.textContent = message || "";
    status.className = isError ? "ai-status ai-status--error" : "ai-status";
  }

  function stripHtml(value) {
    if (!value) return "";
    const wrapper = document.createElement("div");
    wrapper.innerHTML = value;
    return wrapper.textContent || wrapper.innerText || "";
  }

  function formatPrice(activity) {
    if (activity.priceAmount == null) return "Not specified";
    const amountValue = Number(activity.priceAmount);
    if (!Number.isFinite(amountValue)) return "Not specified";
    if (amountValue === 0) return "Free";
    const amount = amountValue.toFixed(2);
    const currency = activity.priceCurrency || "";
    return `${amount} ${currency}`.trim();
  }

  function formatCoordinate(value) {
    if (value == null) return "Not specified";
    const numberValue = Number(value);
    if (!Number.isFinite(numberValue)) return "Not specified";
    return numberValue.toFixed(6);
  }

  function createMetaLine(label, value, linkUrl) {
    const line = document.createElement("p");
    const strong = document.createElement("span");
    strong.className = "ai-activity-label";
    strong.textContent = `${label}: `;
    line.appendChild(strong);

    if (linkUrl) {
      const link = document.createElement("a");
      link.href = linkUrl;
      link.textContent = linkUrl;
      link.target = "_blank";
      link.rel = "noopener";
      link.className = "ai-activity-link";
      line.appendChild(link);
    } else {
      const text = document.createElement("span");
      text.textContent = value;
      line.appendChild(text);
    }

    return line;
  }

  function buildActivityMeta(activity) {
    const meta = document.createElement("div");
    meta.className = "ai-activity-meta";

    const fields = [
      ["Address", activity.address || "Not specified"],
      ["Rating", activity.rating != null ? activity.rating : "Not specified"],
      ["Website", activity.websiteUri || "Not specified"],
      ["Type", activity.type || "Not specified"],
      ["Duration", activity.minimumDuration || "Not specified"],
      ["Price", formatPrice(activity)],
      ["Latitude", formatCoordinate(activity.latitude)],
      ["Longitude", formatCoordinate(activity.longitude)]
    ];

    fields.forEach(([label, value]) => {
      const linkUrl = label === "Website" && activity.websiteUri ? activity.websiteUri : null;
      meta.appendChild(createMetaLine(label, value, linkUrl));
    });

    return meta;
  }

  function buildActivityActions(activity) {
    const actions = document.createElement("div");
    actions.className = "ai-activity-actions";

    if (activity.source === "google" && activity.name) {
      const details = document.createElement("a");
      details.className = "btn btn-primary";
      details.href = `/places/${encodeURIComponent(activity.name)}`;
      details.textContent = "See Details";
      actions.appendChild(details);
    }

    if (activity.latitude != null && activity.longitude != null) {
      const map = document.createElement("a");
      map.className = "btn btn-ghost";
      map.href = `https://www.google.com/maps/search/?api=1&query=${activity.latitude},${activity.longitude}`;
      map.target = "_blank";
      map.rel = "noopener";
      map.textContent = "Open in Maps";
      actions.appendChild(map);
    }

    if (activity.source === "amadeus" && activity.bookingLink) {
      const book = document.createElement("a");
      book.className = "btn btn-primary";
      book.href = activity.bookingLink;
      book.target = "_blank";
      book.rel = "noopener";
      book.textContent = "Book Now";
      actions.appendChild(book);
    }

    if (activity.latitude != null && activity.longitude != null) {
      const add = document.createElement("button");
      add.type = "button";
      add.className = "btn btn-ghost";
      add.textContent = "Add to Itinerary";
      add.addEventListener("click", async () => {

      if (!userId) {
        window.location.href = `/user/login`;
        return;
      }

        const destinationValue = getDestinationValue();
        const countryValue = getCountryValue();
        if (!destinationValue || !countryValue) {
          setStatus("Please select a destination first.", true);
          return;
        }
        const payload = {
          name: activity.name,
          address: activity.address || "",
          latitude: activity.latitude,
          longitude: activity.longitude,
          destination: destinationValue,
          country: countryValue,
          userId: userId
        };
        try {
          const res = await fetch("/routes/waypoint/add", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
          });
          const message = await res.text();
          if (!res.ok) {
            setStatus(message || "Error adding waypoint.", true);
            return;
          }
          setStatus(message || "Waypoint added.", false);
        } catch (err) {
          console.error(err);
          setStatus("Error adding waypoint.", true);
        }
      });
      actions.appendChild(add);
    }

    return actions;
  }

  function createActivityCard(activity) {
    const card = document.createElement("div");
    card.className = "ai-activity-card";

    const media = document.createElement("div");
    media.className = "ai-activity-media";
    if (activity.picture) {
      const img = document.createElement("img");
      img.src = activity.picture;
      img.alt = activity.name || "Activity photo";
      img.loading = "lazy";
      media.appendChild(img);
    } else {
      const placeholder = document.createElement("div");
      placeholder.className = "ai-activity-placeholder";
      placeholder.textContent = "No image available";
      media.appendChild(placeholder);
    }

    const content = document.createElement("div");
    content.className = "ai-activity-content";

    const title = document.createElement("h4");
    title.className = "ai-activity-title";
    title.textContent = activity.name || "Activity";
    content.appendChild(title);

    if (activity.description) {
      const desc = document.createElement("p");
      desc.className = "ai-activity-description";
      desc.textContent = stripHtml(activity.description);
      content.appendChild(desc);
    }

    content.appendChild(buildActivityMeta(activity));
    content.appendChild(buildActivityActions(activity));

    card.appendChild(media);
    card.appendChild(content);
    return card;
  }

  function renderActivities(activities) {
    if (!activitiesContainer) return;
    activitiesContainer.innerHTML = "";

    if (Array.isArray(activities) && activities.length > 0) {
      activities.forEach((activity) => {
        activitiesContainer.appendChild(createActivityCard(activity));
      });
      return;
    }

    const empty = document.createElement("p");
    empty.className = "muted";
    empty.textContent = "No activities selected.";
    activitiesContainer.appendChild(empty);
  }

  function parseItinerary(text) {
    if (!text) return [];
    const lines = text
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter(Boolean);

    const days = [];
    let currentDay = null;
    let currentItem = null;

    const dayRegex = /^(Jour|Day)\s*\d+\s*[:-]\s*(.+)$/i;
    const timeRegex = /^(\d{1,2}h\d{2})\s*[:-]\s*(.+)$/i;
    const timeAltRegex = /^(\d{1,2}:\d{2})\s*[:-]\s*(.+)$/i;
    const timeAmRegex = /^(\d{1,2}:\d{2}\s*(?:AM|PM|am|pm))\s*[:-]\s*(.+)$/i;
    const addressRegex = /^(Adresse|Address)\s*:\s*(.+)$/i;

    const flushItem = () => {
      if (currentDay && currentItem) {
        currentDay.items.push(currentItem);
        currentItem = null;
      }
    };

    const flushDay = () => {
      if (currentDay) {
        flushItem();
        days.push(currentDay);
        currentDay = null;
      }
    };

    lines.forEach((raw) => {
      const cleaned = raw.replace(/^[*-]\s*/, "").replaceAll("**", "").trim();

      const dayMatch = cleaned.match(dayRegex);
      if (dayMatch) {
        flushDay();
        currentDay = { title: cleaned, items: [], notes: [] };
        return;
      }

      const lower = cleaned.toLowerCase();
      if (lower.startsWith("jour") || lower.startsWith("day")) {
        flushDay();
        currentDay = { title: cleaned, items: [], notes: [] };
        return;
      }

      const timeMatch = cleaned.match(timeRegex) || cleaned.match(timeAmRegex) || cleaned.match(timeAltRegex);
      if (timeMatch) {
        flushItem();
        currentItem = {
          time: timeMatch[1],
          title: timeMatch[2],
          address: null,
          notes: []
        };
        return;
      }

      const addressMatch = cleaned.match(addressRegex);
      if (addressMatch) {
        if (currentItem) {
          currentItem.address = addressMatch[2];
        } else if (currentDay) {
          currentDay.notes.push(addressMatch[2]);
        }
        return;
      }

      if (currentItem) {
        currentItem.notes.push(cleaned);
      } else if (currentDay) {
        currentDay.notes.push(cleaned);
      }
    });

    flushDay();
    return days;
  }

  function renderItem(item) {
    const row = document.createElement("div");
    row.className = "ai-item";

    const time = document.createElement("div");
    time.className = "ai-item-time";
    time.textContent = item.time;
    row.appendChild(time);

    const body = document.createElement("div");
    body.className = "ai-item-body";

    const name = document.createElement("p");
    name.className = "ai-item-title";
    name.textContent = item.title;
    body.appendChild(name);

    if (item.address) {
      const address = document.createElement("p");
      address.className = "ai-item-address";
      address.textContent = item.address;
      body.appendChild(address);
    }

    if (Array.isArray(item.notes) && item.notes.length) {
      item.notes.forEach((noteText) => {
        const note = document.createElement("p");
        note.className = "ai-item-note";
        note.textContent = noteText;
        body.appendChild(note);
      });
    }

    row.appendChild(body);
    return row;
  }

  function renderDay(day) {
    const card = document.createElement("div");
    card.className = "ai-day";

    const title = document.createElement("h4");
    title.className = "ai-day-title";
    title.textContent = day.title;
    card.appendChild(title);

    if (Array.isArray(day.items) && day.items.length) {
      const list = document.createElement("div");
      list.className = "ai-day-list";
      day.items.forEach((item) => list.appendChild(renderItem(item)));
      card.appendChild(list);
    }

    if (Array.isArray(day.notes) && day.notes.length) {
      const note = document.createElement("p");
      note.className = "ai-day-note";
      note.textContent = day.notes.join(" ");
      card.appendChild(note);
    }

    return card;
  }

  function renderItinerary(text) {
    if (!output) return;
    output.innerHTML = "";
    output.classList.remove("ai-output--raw");

    if (!text) return;

    const days = parseItinerary(text);
    if (!days.length) {
      output.classList.add("ai-output--raw");
      output.textContent = text;
      return;
    }

    const wrapper = document.createElement("div");
    wrapper.className = "ai-itinerary";
    days.forEach((day) => wrapper.appendChild(renderDay(day)));
    output.appendChild(wrapper);
  }

  function validateForm() {
    if (!getDestinationValue()) {
      setStatus("Please select a destination first.", true);
      return false;
    }
    if (!startDate?.value || !endDate?.value) {
      setStatus("Please select start and end dates.", true);
      return false;
    }
    const start = new Date(startDate.value);
    const end = new Date(endDate.value);
    if (end < start) {
      setStatus("End date must be after start date.", true);
      return false;
    }
    const promptValue = promptInput?.value ?? "";
    if (promptValue.trim().length === 0) {
      setStatus("Please describe your preferences for the AI.", true);
      return false;
    }
    return true;
  }

  async function requestItinerary() {
    if (!validateForm()) return;

    setStatus("Generating your itinerary...", false);
    result.hidden = true;
    output.innerHTML = "";
    output.classList.remove("ai-output--raw");
    feasibility.textContent = "";
    renderActivities([]);

    const payload = {
      prompt: promptInput.value.trim(),
      destination: getDestinationValue(),
      startDate: startDate.value,
      endDate: endDate.value,
      latitude: readNumber("geo-latitude"),
      longitude: readNumber("geo-longitude"),
      countryCode: getCountryValue() || null,
      filters: readFilters()
    };

    try {
      const res = await fetch("/api/chat/itinerary", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      const contentType = res.headers.get("content-type") || "";
      let data = null;
      let text = null;
      if (contentType.includes("application/json")) {
        data = await res.json();
      } else {
        text = await res.text();
      }

      if (!res.ok) {
        const message = data?.error || text || "Failed to generate itinerary.";
        setStatus(message, true);
        return;
      }

      renderItinerary(data?.itinerary || "");
      feasibility.textContent = data?.feasibility || "";
      renderActivities(data?.activities);
      result.hidden = false;
      setStatus("", false);
    } catch (err) {
      console.error(err);
      setStatus("Something went wrong while contacting the AI.", true);
    }
  }

  generateButton?.addEventListener("click", requestItinerary);

  viewItinerary?.addEventListener("click", () => {
    const destinationValue = getDestinationValue();
    const countryValue = getCountryValue();
    if (!destinationValue || !countryValue) {
      setStatus("Please select a destination first.", true);
      return;
    }

    if (userId == null) {
      window.location.href = `/user/login`;
      return;
    }
    
    const url = '/user/itineraries?destination=' + encodeURIComponent(destinationValue) +
      '&country=' + encodeURIComponent(countryValue);

    globalThis.location.href = url;
  });
})();
