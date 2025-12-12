let city = "";

document.addEventListener('DOMContentLoaded', () => {
    city = document.getElementById('city').value;
    const descriptions = getDescriptions(city);

    const places = Array.from(document.querySelectorAll('.place-item'));

    if (places.length === 0) return;

    const mainContainer = document.createElement('div');
    mainContainer.id = 'places-sections';

    const sections = {};

    for (const place of places) {
        const typeElement = place.querySelector('.place-type');
        const type = typeElement.textContent.trim();

        if (!sections[type]) {
            sections[type] = createDivSection(type, descriptions[type]);
            mainContainer.appendChild(sections[type]);
        }

        sections[type].appendChild(place);
    }

    const header = document.querySelector('.header-section');
    if (header) {
        header.insertAdjacentElement('afterend', mainContainer);
    }
});

  
function createDivSection(type, descriptionText) {
    const div = document.createElement('div');
    div.classList.add('place-description-section');
    div.id = type + '-description-section';

    const headerDiv = document.createElement('div');
    headerDiv.classList.add('place-description-header');
    div.appendChild(headerDiv);

    const header = document.createElement('h2');
    header.textContent = placeTypes[type] || type;
    headerDiv.appendChild(header);

    if (descriptionText) {
        const paragraph = document.createElement('p');
        paragraph.textContent = descriptionText;
        headerDiv.appendChild(paragraph);
    }

    return div;
}
