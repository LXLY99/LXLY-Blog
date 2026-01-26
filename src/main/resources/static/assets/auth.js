const bgImages = [
  'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1400&q=80',
  'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1400&q=80',
  'https://images.unsplash.com/photo-1470770841072-f978cf4d019e?auto=format&fit=crop&w=1400&q=80',
];

const screen = document.getElementById('auth-screen');
const loginForm = document.getElementById('login-form');
const extra = document.getElementById('auth-extra');
const openRegister = document.getElementById('open-register');
const openReset = document.getElementById('open-reset');
const switchBg = document.getElementById('switch-bg');

let bgIndex = Math.floor(Math.random() * bgImages.length);

function applyBackground() {
  screen.style.backgroundImage = `url(${bgImages[bgIndex]})`;
}

function setExtra(content) {
  extra.innerHTML = content;
}

function renderRegister() {
  setExtra(`
    <div class="auth-extra">
      <h3>注册新账号</h3>
      <form class="form" id="register-form">
        <div class="form-row">
          <label>昵称</label>
          <input class="input" name="nickname" required />
        </div>
        <div class="form-row">
          <label>邮箱</label>
          <input class="input" name="email" type="email" required />
        </div>
        <div class="form-row">
          <label>验证码</label>
          <div class="search-bar">
            <input class="input" name="code" required />
            <button class="button ghost" type="button" id="send-register-code">发送</button>
          </div>
        </div>
        <div class="form-row">
          <label>密码</label>
          <input class="input" name="password" type="password" required />
        </div>
        <button class="button" type="submit">注册并登录</button>
      </form>
    </div>
  `);

  document.getElementById('send-register-code').addEventListener('click', async () => {
    const email = extra.querySelector('[name=email]').value;
    if (!email) return App.showToast('请输入邮箱');
    try {
      await App.apiFetch('/api/auth/code/register', {
        method: 'POST',
        body: JSON.stringify({ email }),
      });
      App.showToast('验证码已发送');
    } catch (err) {
      App.showToast(err.message);
    }
  });

  extra.querySelector('#register-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    try {
      const data = await App.apiFetch('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      App.setToken(data.token);
      window.location.href = '/home.html';
    } catch (err) {
      App.showToast(err.message);
    }
  });
}

function renderReset() {
  setExtra(`
    <div class="auth-extra">
      <h3>重置密码</h3>
      <form class="form" id="reset-form">
        <div class="form-row">
          <label>邮箱</label>
          <input class="input" name="email" type="email" required />
        </div>
        <div class="form-row">
          <label>验证码</label>
          <div class="search-bar">
            <input class="input" name="code" required />
            <button class="button ghost" type="button" id="send-reset-code">发送</button>
          </div>
        </div>
        <div class="form-row">
          <label>新密码</label>
          <input class="input" name="newPassword" type="password" required />
        </div>
        <button class="button" type="submit">重置密码</button>
      </form>
    </div>
  `);

  document.getElementById('send-reset-code').addEventListener('click', async () => {
    const email = extra.querySelector('[name=email]').value;
    if (!email) return App.showToast('请输入邮箱');
    try {
      await App.apiFetch('/api/auth/code/reset-password', {
        method: 'POST',
        body: JSON.stringify({ email }),
      });
      App.showToast('验证码已发送');
    } catch (err) {
      App.showToast(err.message);
    }
  });

  extra.querySelector('#reset-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    try {
      await App.apiFetch('/api/auth/reset-password', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      App.showToast('密码已重置，请登录');
      setExtra('');
    } catch (err) {
      App.showToast(err.message);
    }
  });
}

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(loginForm).entries());
  try {
    const data = await App.apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    App.setToken(data.token);
    window.location.href = '/home.html';
  } catch (err) {
    App.showToast(err.message);
  }
});

openRegister.addEventListener('click', renderRegister);
openReset.addEventListener('click', renderReset);

switchBg.addEventListener('click', () => {
  bgIndex = (bgIndex + 1) % bgImages.length;
  applyBackground();
});

applyBackground();
