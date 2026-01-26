App.requireAuth();
App.initNav();

const profileForm = document.getElementById('profile-form');
const passwordForm = document.getElementById('password-form');
const avatarEl = document.getElementById('profile-avatar');
const nameEl = document.getElementById('profile-name');
const emailEl = document.getElementById('profile-email');

async function loadProfile() {
  try {
    const data = await App.apiFetch('/api/me');
    avatarEl.src = data.avatar || 'https://placehold.co/80x80';
    nameEl.textContent = data.nickname || '未命名用户';
    emailEl.textContent = data.email || '';
    profileForm.nickname.value = data.nickname || '';
    profileForm.gender.value = data.gender || '';
    profileForm.avatar.value = data.avatar || '';
    profileForm.avatarDeleteHash.value = data.avatarDeleteHash || '';
  } catch (err) {
    App.showToast(err.message);
  }
}

profileForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(profileForm).entries());
  if (payload.gender === '') {
    delete payload.gender;
  } else {
    payload.gender = Number(payload.gender);
  }
  try {
    await App.apiFetch('/api/me/profile', {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
    App.showToast('资料已更新');
    await loadProfile();
  } catch (err) {
    App.showToast(err.message);
  }
});

passwordForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(passwordForm).entries());
  try {
    await App.apiFetch('/api/me/password', {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
    App.showToast('密码已更新，请重新登录');
    localStorage.removeItem('token');
    window.location.href = '/auth.html';
  } catch (err) {
    App.showToast(err.message);
  }
});

loadProfile();
