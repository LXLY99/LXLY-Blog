App.requireAuth();
App.initNav();

const profileForm = document.getElementById('profile-form');
const passwordForm = document.getElementById('password-form');
const avatarEl = document.getElementById('profile-avatar');
const nameEl = document.getElementById('profile-name');
const emailEl = document.getElementById('profile-email');
const avatarUpload = document.getElementById('avatar-upload');
const avatarResult = document.getElementById('avatar-upload-result');
const coupleBgUpload = document.getElementById('couple-bg-upload');
const coupleBgResult = document.getElementById('couple-bg-result');

async function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  const response = await fetch('/api/upload/image', {
    method: 'POST',
    headers: { ...App.getToken() ? { Authorization: `Bearer ${App.getToken()}` } : {} },
    body: formData,
  });
  const data = await response.json();
  if (data.code !== 0) throw new Error(data.message || '上传失败');
  return data.data;
}

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

avatarUpload.addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const result = await uploadImage(file);
    profileForm.avatar.value = result.url;
    profileForm.avatarDeleteHash.value = result.deleteHash;
    avatarEl.src = result.url;
    avatarResult.textContent = '头像已上传，请保存资料。';
  } catch (err) {
    avatarResult.textContent = err.message;
  }
});

coupleBgUpload.addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const result = await uploadImage(file);
    localStorage.setItem('coupleBackground', result.url);
    coupleBgResult.textContent = '情侣背景已更新。';
  } catch (err) {
    coupleBgResult.textContent = err.message;
  }
});

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
    window.location.href = '/index.html';
  } catch (err) {
    App.showToast(err.message);
  }
});

loadProfile();
