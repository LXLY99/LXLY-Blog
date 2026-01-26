const API_BASE = '';

function getToken() {
  return localStorage.getItem('token');
}

function setToken(token) {
  localStorage.setItem('token', token);
}

function authHeaders() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function apiFetch(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...authHeaders(),
    ...(options.headers || {}),
  };
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.code !== 0) {
    const message = data.message || '请求失败';
    throw new Error(message);
  }
  return data.data;
}

function formatDate(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString();
}

function showToast(message) {
  const toast = document.createElement('div');
  toast.textContent = message;
  toast.style.position = 'fixed';
  toast.style.bottom = '24px';
  toast.style.right = '24px';
  toast.style.padding = '12px 16px';
  toast.style.background = '#1f2937';
  toast.style.color = '#fff';
  toast.style.borderRadius = '10px';
  toast.style.zIndex = 9999;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 2500);
}

function requireAuth() {
  if (!getToken()) {
    window.location.href = '/index.html';
  }
}

function initNav() {
  const logoutBtn = document.querySelector('[data-logout]');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      try {
        await apiFetch('/api/me/logout', { method: 'POST' });
      } catch (err) {
        // ignore
      } finally {
        localStorage.removeItem('token');
        window.location.href = '/index.html';
      }
    });
  }
}

window.App = {
  apiFetch,
  setToken,
  getToken,
  showToast,
  formatDate,
  requireAuth,
  initNav,
};
