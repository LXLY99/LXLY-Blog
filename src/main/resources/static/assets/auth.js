const tabs = document.querySelectorAll('#auth-tabs .tab');
const content = document.getElementById('auth-content');
const bgUpload = document.getElementById('bg-upload');
const resetBg = document.getElementById('reset-bg');
const visual = document.getElementById('auth-visual');

const templates = {
  login: () => `
    <form class="form" id="login-form">
      <div class="form-row">
        <label>邮箱</label>
        <input class="input" name="email" type="email" required />
      </div>
      <div class="form-row">
        <label>密码</label>
        <input class="input" name="password" type="password" required />
      </div>
      <button class="button" type="submit">登录</button>
    </form>
  `,
  register: () => `
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
  `,
  reset: () => `
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
  `,
};

function applyBackground(url) {
  if (url) {
    visual.style.backgroundImage = `url(${url})`;
  } else {
    visual.style.backgroundImage = 'url(https://images.unsplash.com/photo-1529333166437-7750a6dd5a70?auto=format&fit=crop&w=1200&q=80)';
  }
}

function setActiveTab(name) {
  tabs.forEach((tab) => {
    tab.classList.toggle('active', tab.dataset.tab === name);
  });
  content.innerHTML = templates[name]();

  if (name === 'register') {
    document.getElementById('send-register-code').addEventListener('click', async () => {
      const email = content.querySelector('[name=email]').value;
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
  }

  if (name === 'reset') {
    document.getElementById('send-reset-code').addEventListener('click', async () => {
      const email = content.querySelector('[name=email]').value;
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
  }

  const form = content.querySelector('form');
  if (form) {
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(form);
      const payload = Object.fromEntries(formData.entries());

      try {
        if (name === 'login') {
          const data = await App.apiFetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify(payload),
          });
          App.setToken(data.token);
          window.location.href = '/blog.html';
        }
        if (name === 'register') {
          const data = await App.apiFetch('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify(payload),
          });
          App.setToken(data.token);
          window.location.href = '/blog.html';
        }
        if (name === 'reset') {
          await App.apiFetch('/api/auth/reset-password', {
            method: 'POST',
            body: JSON.stringify(payload),
          });
          App.showToast('密码已重置，请登录');
          setActiveTab('login');
        }
      } catch (err) {
        App.showToast(err.message);
      }
    });
  }
}

tabs.forEach((tab) => {
  tab.addEventListener('click', () => setActiveTab(tab.dataset.tab));
});

bgUpload.addEventListener('change', (event) => {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    localStorage.setItem('authBackground', reader.result);
    applyBackground(reader.result);
  };
  reader.readAsDataURL(file);
});

resetBg.addEventListener('click', () => {
  localStorage.removeItem('authBackground');
  applyBackground(null);
});

applyBackground(localStorage.getItem('authBackground'));
setActiveTab('login');
