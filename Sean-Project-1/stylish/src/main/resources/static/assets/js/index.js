document.addEventListener('DOMContentLoaded', () => {
    fetch('http://18.182.254.152/api/1.0/marketing/campaigns')
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('campaign-container');
            const campaign = data.data[0];
            if (campaign) {
                const campaignContainer = document.createElement('div');
                campaignContainer.className = 'campaign-container';
                const campaignlink = document.createElement('a');
                campaignlink.href = `product.html?id=${campaign.product_id}`;
                const campaignImg = document.createElement('img');
                campaignImg.style.backgroundImage = `url(${campaign.picture})`;
                campaignImg.className = "campaign-img";

                campaignlink.appendChild(campaignImg)
                campaignContainer.appendChild(campaignlink);

                const storyDiv = document.createElement('div');
                storyDiv.className = 'campaign-story';
                const storyLines = campaign.story.split('<<NEWLINE>>');
                storyLines.forEach(line => {
                    if (line.trim() !== '') {
                        const p = document.createElement('p');
                        p.textContent = line.trim();
                        storyDiv.appendChild(p);
                    }
                });
                campaignContainer.appendChild(storyDiv);
                container.appendChild(campaignContainer);
            } else {
                console.error('No campaign found in the response data.');
            }
        })
        .catch(error => console.error('Error fetching data:', error));

    function loadProducts(category) {
        const apiUrl = `http://18.182.254.152/api/1.0/products/${category}`;
        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                const productContainer = document.getElementById('product-container');
                productContainer.innerHTML = '';

                data.data.forEach(product => {
                    const productDiv = document.createElement('div');
                    productDiv.className = "product-wrap";

                    const productLink = document.createElement('a');
                    productLink.href = `product.html?id=${product.id}`;

                    const img = document.createElement('img');
                    img.className = "product-img";
                    img.src = product.main_image;
                    img.alt = "Product Image";

                    productLink.appendChild(img);

                    const colorsDiv = document.createElement('div');
                    colorsDiv.className = "product-color-div";
                    const colors = product.colors;
                    colors.forEach(color => {
                        const colorDiv = document.createElement('div');
                        colorDiv.className = 'color';
                        colorDiv.style.backgroundColor = "#" + color.code;
                        colorsDiv.appendChild(colorDiv);
                    });

                    const name = document.createElement('a');
                    name.className = "product-name";
                    name.href = `product.html?id=${product.id}`;
                    name.textContent = product.title;

                    const price = document.createElement("p");
                    price.className = "product-price";
                    price.textContent = "TWD . " + product.price;

                    productDiv.appendChild(productLink);
                    productDiv.appendChild(colorsDiv);
                    productDiv.appendChild(name);
                    productDiv.appendChild(price);
                    productContainer.appendChild(productDiv);
                });
            })
            .catch(error => console.error('Error fetching data:', error));
    }

    const urlParams = new URLSearchParams(window.location.search);
    const categoryParam = urlParams.get('category');
    if (categoryParam) {
        loadProducts(categoryParam);
    } else {
        loadProducts('all');
    }

    const navLinks = document.querySelectorAll('nav ul li a');
    navLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const category = link.getAttribute('data-category');
            if (category) {
                const newUrl = `index.html?category=${category}`;
                history.pushState({}, '', newUrl);
                loadProducts(category);
            }
        });
    });
});
