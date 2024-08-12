document.addEventListener('DOMContentLoaded', () => {

    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (productId) {

        fetch(`http://18.182.254.152/api/1.0/products/details?id=${productId}`)
            .then(response => response.json())
            .then(data => {
                const productInfo = data.data;
                const productContainer = document.getElementById('product-container');

                if (data && productInfo) {
                    const majorInfo = document.createElement('div');
                    majorInfo.className = "majorInfo-container";

                    const productImg = document.createElement('img');
                    productImg.className = "product-img";
                    productImg.src = productInfo.main_image;

                    const info = document.createElement('div');
                    info.className = "product-info";

                    const productName = document.createElement('p');
                    productName.textContent = productInfo.title;
                    productName.className = "product-name";
                    info.appendChild(productName);

                    const productId = document.createElement('p');
                    productId.textContent = "商品編號 : " + productInfo.id;
                    productId.className = "product-id";
                    info.appendChild(productId);

                    const productPrice = document.createElement('p');
                    productPrice.textContent = "TWD." + productInfo.price;
                    productPrice.className = "product-price";
                    info.appendChild(productPrice);

                    const hr1 = document.createElement('hr');
                    hr1.className = "hr1";
                    info.appendChild(hr1);

                    const colorsDiv = document.createElement('div');
                    colorsDiv.className = "product-colors-container";
                    const colorTitle = document.createElement('p');
                    colorTitle.textContent = "顏色";
                    colorTitle.className = "color-title";
                    colorsDiv.appendChild(colorTitle);

                    const colors = productInfo.colors;
                    colors.forEach(color => {

                        const colorDiv = document.createElement('div');
                        colorDiv.className = "color-div";
                        colorDiv.setAttribute('value', color.code);

                        const colorButton = document.createElement('button');
                        colorButton.style.backgroundColor = "#" + color.code;
                        colorButton.className = "color-button";
                        colorButton.setAttribute('data-color', color.code);
                        colorDiv.appendChild(colorButton);
                        colorsDiv.appendChild(colorDiv);
                    });
                    info.appendChild(colorsDiv);

                    const sizesDiv = document.createElement('div');
                    sizesDiv.className = "product-sizes-container";
                    const sizeTitle = document.createElement('p');
                    sizeTitle.textContent = '尺寸';
                    sizesDiv.appendChild(sizeTitle);

                    const sizes = ["S", "M", "L"];
                    sizes.forEach(size => {

                        const sizeDiv = document.createElement('div');
                        sizeDiv.className = "size-div";
                        sizeDiv.setAttribute('value', size);

                        const sizeButton = document.createElement('button');
                        sizeButton.className = "size-button";
                        sizeButton.textContent = size;
                        sizeButton.setAttribute('data-size', size);
                        sizeDiv.appendChild(sizeButton);
                        sizesDiv.appendChild(sizeDiv);
                    });
                    info.appendChild(sizesDiv);

                    const countsdDiv = document.createElement('div');
                    countsdDiv.className = "count-container";
                    const countTitle = document.createElement('p');
                    countTitle.className = "count-title";
                    countTitle.textContent = "數量";
                    const countDiv = document.createElement('div');
                    countDiv.className = "count-wrap";

                    const minus = document.createElement('button');
                    minus.textContent = "-";
                    minus.className = "minus";

                    const amount = document.createElement('input');
                    amount.className = "amount";
                    amount.type = "text";
                    amount.value = "0";
                    amount.readOnly = true;

                    const plus = document.createElement('button');
                    plus.className = "plus";
                    plus.textContent = "+";

                    countDiv.appendChild(minus);
                    countDiv.appendChild(amount);
                    countDiv.appendChild(plus);
                    countsdDiv.appendChild(countTitle);
                    countsdDiv.appendChild(countDiv);
                    info.appendChild(countsdDiv);

                    const buy = document.createElement("button");
                    buy.textContent = "請先選擇商品顏色,尺寸及數量";
                    buy.className = "buy-button";
                    buy.disabled = true;
                    info.appendChild(buy);

                    const note = document.createElement('p');
                    note.className = "note";
                    note.textContent = productInfo.note;
                    info.appendChild(note);

                    const texture = document.createElement('p');
                    texture.className = "texture";
                    texture.textContent = "材質 : " + productInfo.texture;
                    info.appendChild(texture);

                    const thickness = document.createElement('p');
                    thickness.className = "thickness";
                    thickness.textContent = "厚薄 : 適中";
                    info.appendChild(thickness);

                    const elasticity = document.createElement('p');
                    elasticity.className = "elasticity";
                    elasticity.textContent = "彈性 : 適中";
                    info.appendChild(elasticity);

                    const wash = document.createElement('p');
                    wash.className = "wash";
                    wash.textContent = "洗滌 : " + productInfo.wash;
                    info.appendChild(wash);

                    const place = document.createElement('p');
                    place.className = "place";
                    place.textContent = "產地 : " + productInfo.place;
                    info.appendChild(place);

                    majorInfo.appendChild(productImg);
                    majorInfo.appendChild(info);

                    const secondaryInfo = document.createElement('div');
                    secondaryInfo.className = "secondaryInfo-container";

                    const detailsTitleContainer = document.createElement('div');
                    detailsTitleContainer.className = "details-title-container";
                    const detailsTitle = document.createElement('p');
                    detailsTitle.className = "details-title";
                    detailsTitle.textContent = "更多產品資訊";
                    detailsTitleContainer.appendChild(detailsTitle);
                    const detailsLine = document.createElement('hr');
                    detailsLine.className = "details-line";
                    detailsTitleContainer.appendChild(detailsLine);
                    secondaryInfo.appendChild(detailsTitleContainer);

                    const description = document.createElement('p');
                    description.className = "description";
                    description.textContent = productInfo.description;
                    secondaryInfo.appendChild(description);

                    const images = productInfo.images;
                    console.log(images)
                    images.forEach(image => {
                        const img = document.createElement('img');
                        img.src = image;
                        img.className = "details-image";
                        secondaryInfo.appendChild(img);
                    });

                    productContainer.appendChild(majorInfo);
                    productContainer.appendChild(secondaryInfo);

                    let selectedColor = null;
                    let selectedSize = null;

                    colorsDiv.addEventListener('click', (e) => {
                        const colorButton = e.target.closest('.color-button');
                        if (colorButton) {
                            colorsDiv.querySelectorAll('.color-button').forEach(btn => {
                                btn.classList.remove('focused');
                            });
                            colorButton.classList.add('focused');
                            selectedColor = colorButton.getAttribute('data-color');
                            updateSizeAvailability();
                            checkBuyButtonState();
                        }
                    });


                    sizesDiv.addEventListener('click', (e) => {
                        const sizeButton = e.target.closest('.size-button');
                        if (sizeButton && !sizeButton.disabled) {
                            sizesDiv.querySelectorAll('.size-button').forEach(btn => {
                                btn.classList.remove('selected');
                            });
                            sizeButton.classList.add('selected');
                            selectedSize = sizeButton.getAttribute('data-size');
                            checkBuyButtonState();
                        }
                    });

                    function updateSizeAvailability() {
                        if (selectedColor) {
                            const availableSizes = productInfo.variants
                                .filter(v => v.color_code === selectedColor)
                                .map(v => v.size);

                            sizesDiv.querySelectorAll('.size-button').forEach(btn => {
                                const size = btn.getAttribute('data-size');
                                if (availableSizes.includes(size)) {
                                    btn.disabled = false;
                                    btn.style.opacity = "1";
                                } else {
                                    btn.disabled = true;
                                    btn.style.opacity = "0.5";
                                    btn.classList.remove('selected');
                                }
                            });
                        }
                    }

                    let quantity = 0;

                    function checkBuyButtonState() {
                        if (selectedColor && selectedSize && quantity > 0) {
                            buy.disabled = false;
                            buy.textContent = "購買商品";
                        } else {
                            buy.disabled = true;
                            buy.textContent = "請先選擇商品顏色, 尺寸及數量";
                        }
                    }

                    plus.addEventListener('click', () => {
                        if (quantity < 10) {
                            quantity++;
                            amount.value = quantity;
                            checkBuyButtonState();
                        } else {
                            buy.disabled = true;
                            buy.textContent = "已超過商品可購買數量";
                        }
                    });

                    minus.addEventListener('click', () => {
                        if (quantity > 0) {
                            quantity--;
                            amount.value = quantity;
                            checkBuyButtonState();
                        }
                        if (quantity < 10) {
                            checkBuyButtonState();
                        }
                    });
                    buy.addEventListener('click', async () => {
                        const token = localStorage.getItem('jwtToken');
                        if (token) {
                            try {
                                const response = await fetch('http://18.182.254.152/api/1.0/user/profile', {
                                    method: 'GET',
                                    headers: {
                                        'Authorization': `Bearer ${token}`
                                    }
                                });
                                if (response.ok) {
                                    // JWT token is valid, show popup
                                    const checkout = document.querySelector(".checkout-container");
                                    checkout.innerHTML = `
                                        <div class="modal">
                                            <div class="modal-container">
                                                <div class="modal-content">
                                                    <span class = "close-btn">&times;</span>
                                                    <div class = "form-container">
                                                        <p>訂購商品明細</p>
                                                        <hr>
                                                        <p class = "order-productName"></p>
                                                        <p class = "order-variant"></p>
                                                        <p class = "order-amount"></p>
                                                        <p class = "order-price"></p>
                                                        <p class = "order-total"></p>
                                                        <p>信用卡付款</p>
                                                        <hr>
                                                        <div class="form-group card-number-group">
                                                            <label for="card-number" class="control-label"><span id="cardtype"></span>卡號</label>
                                                            <div class="form-control card-number" ></div>
                                                        </div>
                                                        <div class="form-group expiration-date-group">
                                                            <label for="expiration-date" class="control-label">卡片到期日</label>
                                                            <div class="form-control expiration-date" id="tappay-expiration-date"></div>
                                                        </div>
                                                        <div class="form-group ccv-group">
                                                            <label for="ccv" class="control-label">卡片後三碼</label>
                                                            <div class="form-control ccv"></div>
                                                        </div>
                                                        <button type="submit" class="btn btn-default pay-button">付 款</button>
                                                    </div>
                                                </div>    
                                            </div>
                                        </div>
                                    `;

                                    //show ckeckout
                                    //checkout.style.display="flex";
                                    document.querySelector('.modal').style.display = 'flex';
                                    //close button
                                    const closeButton = document.querySelector(".close-btn");
                                    closeButton.addEventListener('click',() => {
                                        document.querySelector('.modal').style.display = 'none';
                                    });

                                    const orderProductName = document.querySelector(".order-productName");
                                    orderProductName.textContent="商品名稱 : " + productInfo.title;
                                    const orderVariant = document.querySelector(".order-variant");
                                    orderVariant.textContent = "規格 : " + selectedColor + "/"+ selectedSize;
                                    const orderAmount = document.querySelector(".order-amount");
                                    orderAmount.textContent = "數量 : " + amount.value;
                                    const orderPrice = document.querySelector('.order-price');
                                    orderPrice.textContent = "單價 : " + productInfo.price;
                                    const orderTotal = document.querySelector(".order-total");
                                    orderTotal.textContent = "總價 : " + (amount.value)*(productInfo.price);

                                    TPDirect.setupSDK(12348, 'app_pa1pQcKoY22IlnSXq5m5WP5jFKzoRG58VEXpT7wU62ud7mMbDOGzCYIlzzLF', 'sandbox')
                                    TPDirect.card.setup({
                                        fields: {
                                            number: {
                                                element: '.form-control.card-number',
                                                placeholder: '**** **** **** ****'
                                            },
                                            expirationDate: {
                                                element: document.getElementById('tappay-expiration-date'),
                                                placeholder: 'MM / YY'
                                            },
                                            ccv: {
                                                element: document.querySelector('.form-control.ccv'),
                                                placeholder: '後三碼'
                                            }
                                        },
                                        styles: {
                                            'input': {
                                                'color': 'gray',
                                            },
                                            'input.ccv': {
                                            },
                                            ':focus': {
                                                'color': 'black'
                                            },
                                            '.valid': {
                                                'color': 'green'
                                            },
                                            '.invalid': {
                                                'color': 'red'
                                            },
                                            '@media screen and (max-width: 400px)': {
                                                'input': {
                                                    'color': 'orange'
                                                }
                                            }
                                        },
                                        isMaskCreditCardNumber: true,
                                        maskCreditCardNumberRange: {
                                            beginIndex: 6,
                                            endIndex: 11
                                        }
                                    });

                                    TPDirect.card.onUpdate(function (update) {
                                        const payButton = document.querySelector('.pay-button');
                                        if (update.canGetPrime) {
                                            payButton.removeAttribute('disabled');
                                        } else {
                                            payButton.setAttribute('disabled', true);
                                        }
                                    });

                                    document.querySelector('.pay-button').addEventListener('click', function (event) {
                                        event.preventDefault();

                                        const tappayStatus = TPDirect.card.getTappayFieldsStatus();
                                        if (!tappayStatus.canGetPrime) {
                                            alert('請檢查信用卡資訊');
                                            return;
                                        }

                                        TPDirect.card.getPrime((result) => {
                                            if (result.status !== 0) {
                                                alert("付款失敗，請重新付款!");
                                                return;
                                            }
                                            const orderData ={
                                                prime: result.card.prime,
                                                order:{
                                                    shipping:"delivery",
                                                    payment: "credit_card",
                                                    subtotal: 1234,
                                                    freight: 14,
                                                    total: 1300,
                                                    recipient: {
                                                        name: "Luke",
                                                        phone: "0987654321",
                                                        email: "luke@gmail.com",
                                                        address: "市政府站",
                                                        time: "morning"
                                                    },
                                                    list: [
                                                        {
                                                            id: "4",
                                                            name: "春季花卉裙",
                                                            price: 1299,
                                                            color: {
                                                                code: "0000FF",
                                                                name: "藍色"
                                                            },
                                                            size: "S",
                                                            qty: 0
                                                        }
                                                    ]
                                                }
                                            };

                                            fetch('http://18.182.254.152/api/1.0/order/checkout', {
                                                method: 'POST',
                                                headers: {
                                                    'Content-Type': 'application/json',
                                                    'Authorization': `Bearer ${token}`
                                                },
                                                body:JSON.stringify(orderData)
                                            }).then(response => response.json())
                                                .then(data => {
                                                    document.querySelector('.modal').style.display = 'none';
                                                    alert('訂單成立');
                                                    window.location.href = 'thankyou.html';
                                                }).catch(error => {
                                                alert('訂單失敗，請重新付款');
                                            });
                                        });
                                    });

                                } else {
                                    throw new Error('JWT token 驗證失敗');
                                }
                            } catch (error) {
                                alert('購買前請先登入!');
                                localStorage.removeItem('jwtToken');
                                window.location.href = 'profile.html';
                            }
                        } else {
                            alert('購買前請先登入!');
                            window.location.href = 'profile.html';
                        }
                    });

                } else {
                    productContainer.innerHTML = '<p>產品詳細資訊加載失敗。</p>';
                }
            })
            .catch(error => {
                console.error('Error fetching product data:', error);
                const productContainer = document.querySelector('.product-container');
                productContainer.innerHTML = '<p>無法加載產品詳細資訊。</p>';
            });
    } else {
        const productContainer = document.querySelector('.product-container');
        productContainer.innerHTML = '<p>無此產品。</p>';
    }
});
