document.addEventListener('DOMContentLoaded', async () => {
    const formContainer = document.getElementById('form-container');

    // Show loading state
    formContainer.innerHTML = '<img class="loading" src="assets/images/loading.gif">';

    // Verify JWT token
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
                const userProfile = await response.json();
                showUserProfile(userProfile);
                // Exit function, no longer display the login form
                return;
            } else {
                throw new Error('JWT token 驗證失敗');
            }
        } catch (error) {
            console.error('Error:', error);
            localStorage.removeItem('jwtToken');
        }
    }

    // If there is no token or verification fails, display the login/registration form.
    showAuthForm();

    function showAuthForm() {
        formContainer.innerHTML = `
            <h1>Sign In</h1>
            <p class="welcome">Welcome back.</p>
            <hr>
            <form id="auth-form">
                <label for="email">信箱 :</label>
                <input type="email" id="email" name="email" placeholder="email" required>
                <label for="password">密碼 :</label>
                <input type="password" id="password" name="password" placeholder="password" required>
                <button type="submit">登入</button>
                <button type="button" id="toggle-button">註冊</button>
            </form>
        `;

        // Reacquire the element reference.
        const formTitle = formContainer.querySelector('h1');
        const authForm = document.getElementById('auth-form');
        const toggleButton = document.getElementById('toggle-button');

        // Rebind the event.
        authForm.addEventListener('submit', handleFormSubmit);
        toggleButton.addEventListener('click', () => toggleForm(formTitle));
    }

    function toggleForm(formTitle) {
        if (formTitle.textContent === 'Sign In') {
            // switch to signup page
            formContainer.innerHTML = `
                <h1>Sign Up</h1>
                <p class="welcome">Welcome to join us.</p>
                <hr>
                <form id="auth-form">
                    <label for="name">用戶名稱 :</label>
                    <input type="text" id="name" name="name" placeholder="username" required>
                    <label for="email">信箱 :</label>
                    <input type="email" id="email" name="email" placeholder="email" required>
                    <label for="password">密碼 :</label>
                    <input type="password" id="password" name="password" placeholder="password" required>
                    <button type="submit">註冊</button>
                    <button type="button" id="toggle-button"> 返回登入</button>
                </form>
            `;
        } else {
            // switch to signin page
            formContainer.innerHTML = `
                <h1>Sign In</h1>
                <p class="welcome">Welcome back.</p>
                <hr>
                <form id="auth-form">
                    <label for="email">信箱 :</label>
                    <input type="email" id="email" name="email" placeholder="email" required>
                    <label for="password">密碼 :</label>
                    <input type="password" id="password" name="password" placeholder="password" required>
                    <button type="submit">登入</button>
                    <button type="button" id="toggle-button">註冊</button>
                </form>
            `;
        }

        // Reacquire the element reference.
        const formTitleNew = formContainer.querySelector('h1');
        const authForm = document.getElementById('auth-form');
        const toggleButton = document.getElementById('toggle-button');

        // Rebind the event.
        authForm.addEventListener('submit', handleFormSubmit);
        toggleButton.addEventListener('click', () => toggleForm(formTitleNew));
    }

    async function handleFormSubmit(event) {
        event.preventDefault();

        const authForm = event.target;
        const formData = new FormData(authForm);
        const formDataObject = Object.fromEntries(formData.entries());
        const formTitle = formContainer.querySelector('h1');

        // Only add "provider": "native" during login.
        if (formTitle.textContent === 'Sign In') {
            formDataObject.provider = 'native';
        }
        // Convert FormData to JSON format.
        const formDataJSON = JSON.stringify(formDataObject);

        const action = formTitle.textContent === 'Sign In'
            ? 'http://18.182.254.152/api/1.0/user/signin'
            : 'http://18.182.254.152/api/1.0/user/signup';

        try {
            const response = await fetch(action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: formDataJSON
            });

            const result = await response.json();
            if (response.ok) {
                const token = result.data.access_token;
                localStorage.setItem('jwtToken', token);
                if (formTitle.textContent === 'Sign In') {
                    alert('登入成功！');
                } else {
                    alert('註冊成功！');
                }
                const userProfile = await fetchUserProfile(token);
                showUserProfile(userProfile);
            } else {
                throw new Error(result.message || '操作失敗');
            }
        } catch (error) {
            if (formTitle.textContent === 'Sign In') {
                alert('登入失敗，請重新登入');
            } else {
                alert('註冊失敗，請重新註冊');
            }
        }
    }

    async function fetchUserProfile(token) {
        const response = await fetch('http://18.182.254.152/api/1.0/user/profile', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('JWT token 驗證失敗');
        }
    }

    function showUserProfile(profile) {
        formContainer.innerHTML = `
            <div class="profile-container">
                <h1 class="profile-title">個人檔案</h1>
                <img src="assets/images/user.jpg" alt="User Image">
                <p>用戶名稱: ${profile.data.name}</p>
                <p class="adjust">信箱: ${profile.data.email}</p>
                <button id="logout-button">登出</button>
            </div>
        `;

        // Bind logout button event
        document.getElementById('logout-button').addEventListener('click', logout);
    }

    function logout() {
        localStorage.removeItem('jwtToken');
        location.reload();
    }
});






