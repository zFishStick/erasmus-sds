let city = "";

document.addEventListener('DOMContentLoaded', () => {
    const city = document.getElementById('city').value;
    const places = Array.from(document.querySelectorAll('.place-item'));
    if (places.length === 0) return;

    const mainContainer = document.createElement('div');
    mainContainer.id = 'places-sections';

    const sections = {};

    for (const place of places) {
        const typeElement = place.querySelector('.place-type');
        const type = typeElement.textContent.trim();

        const groupKey = typeToGroupMap[type];
        if (!groupKey) continue;

        if (!sections[groupKey]) {
            sections[groupKey] = createGroupSection(
                groupKey,
                placeGroups[groupKey],
                city
            );
            mainContainer.appendChild(sections[groupKey]);
        }

        sections[groupKey].appendChild(place);
    }

    const header = document.querySelector('.header-section');
    if (header) {
        header.insertAdjacentElement('afterend', mainContainer);
    }

    applyFilters();

});
  
function createGroupSection(groupKey, groupConfig, city) {
    const div = document.createElement('div');
    div.classList.add('place-description-section');
    div.id = `${groupKey}-section`;

    const headerDiv = document.createElement('div');
    headerDiv.classList.add('place-description-header');
    div.appendChild(headerDiv);

    const header = document.createElement('h2');
    header.textContent = groupConfig.label;
    headerDiv.appendChild(header);

    if (groupConfig.description) {
        const paragraph = document.createElement('p');
        paragraph.textContent = groupConfig.description(city);
        headerDiv.appendChild(paragraph);
    }

    return div;
}

const activeGroups = new Set(Object.keys(placeGroups));
const activeTypes = new Set(
    Object.values(placeGroups).flatMap(group => group.types)
);

const savedTypes = localStorage.getItem('activeTypes');
if (savedTypes) {
    activeTypes.clear();
    JSON.parse(savedTypes).forEach(type => activeTypes.add(type));
}


function createFilterCheckboxes() {
    const filterContainer = document.getElementById('filter-container');
    filterContainer.innerHTML = '';

    for (const [groupKey, group] of Object.entries(placeGroups)) {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = `filter-group-${groupKey}`;
        checkbox.value = groupKey;
        checkbox.checked = true;

        checkbox.addEventListener('change', () => {
            toggleGroup(groupKey, checkbox.checked);
        });

        const label = document.createElement('label');
        label.htmlFor = checkbox.id;
        label.textContent = group.label;

        const wrapper = document.createElement('div');
        wrapper.appendChild(checkbox);
        wrapper.appendChild(label);

        filterContainer.appendChild(wrapper);
    }
}

createFilterCheckboxes();
syncCheckboxes();

function syncCheckboxes() {
    for (const [groupKey, group] of Object.entries(placeGroups)) {
        const checkbox = document.getElementById(`filter-group-${groupKey}`);
        if (!checkbox) continue;

        checkbox.checked = group.types.some(type => activeTypes.has(type));
    }
}


function toggleGroup(groupKey, isActive) {
    const types = placeGroups[groupKey].types;

    types.forEach(type => {
        if (isActive) {
            activeTypes.add(type);
        } else {
            activeTypes.delete(type);
        }
    });

    applyFilters();
    saveFilters();
}

function saveFilters() {
    localStorage.setItem(
        'activeTypes',
        JSON.stringify([...activeTypes])
    );
}

function applyFilters() {
    const places = document.querySelectorAll('.place-item');

    places.forEach(place => {
        const typeElement = place.querySelector('.place-type');
        const type = typeElement.textContent.trim();

        place.style.display = activeTypes.has(type) ? '' : 'none';
    });

    const sections = document.querySelectorAll('.place-description-section');

    sections.forEach(section => {
        const visiblePlaces = section.querySelectorAll(
            '.place-item:not([style*="display: none"])'
        );

        section.style.display = visiblePlaces.length > 0 ? '' : 'none';
    });
}

