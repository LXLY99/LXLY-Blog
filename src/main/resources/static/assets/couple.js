App.requireAuth();
App.initNav();

const statusEl = document.getElementById('relation-status');
const pendingEl = document.getElementById('pending-invites');
const inviteForm = document.getElementById('invite-form');
const refreshBtn = document.getElementById('refresh-status');
const breakupBtn = document.getElementById('breakup-btn');
const albumForm = document.getElementById('album-form');
const albumListEl = document.getElementById('album-list');
const albumCoverInput = document.getElementById('album-cover');
const albumCoverResult = document.getElementById('album-cover-result');
const photoForm = document.getElementById('photo-form');
const photoUploadInput = document.getElementById('photo-upload');
const photoUploadResult = document.getElementById('photo-upload-result');
const calendarForm = document.getElementById('calendar-form');
const calendarList = document.getElementById('calendar-list');
const todoForm = document.getElementById('todo-form');
const todoList = document.getElementById('todo-list');
const messageForm = document.getElementById('message-form');
const messageList = document.getElementById('message-list');
const milestoneForm = document.getElementById('milestone-form');
const milestoneList = document.getElementById('milestone-list');
const dateForm = document.getElementById('date-form');
const dateList = document.getElementById('date-list');
const hero = document.getElementById('couple-hero');

const coupleBg = localStorage.getItem('coupleBackground');
if (coupleBg) {
  hero.style.backgroundImage = `url(${coupleBg})`;
}

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

async function loadStatus() {
  try {
    const data = await App.apiFetch('/api/couple/status');
    if (data.active) {
      statusEl.innerHTML = `关系状态：<strong>${data.active.status}</strong> (ID ${data.active.id})`;
    } else {
      statusEl.textContent = '暂无有效情侣关系';
    }

    if (!data.pendingInvites || !data.pendingInvites.length) {
      pendingEl.innerHTML = '<div class="muted">暂无待处理邀请</div>';
      return;
    }
    pendingEl.innerHTML = data.pendingInvites
      .map(
        (invite) => `
        <div class="list-item">
          邀请 ID: ${invite.id} 来自用户 ${invite.requesterId}
          <div style="margin-top: 8px; display: flex; gap: 8px;">
            <button class="button" data-accept="${invite.id}">接受</button>
            <button class="button ghost" data-reject="${invite.id}">拒绝</button>
          </div>
        </div>
      `
      )
      .join('');

    pendingEl.querySelectorAll('[data-accept]').forEach((btn) => {
      btn.addEventListener('click', () => handleInvite('accept', btn.dataset.accept));
    });
    pendingEl.querySelectorAll('[data-reject]').forEach((btn) => {
      btn.addEventListener('click', () => handleInvite('reject', btn.dataset.reject));
    });
  } catch (err) {
    statusEl.textContent = err.message;
  }
}

async function handleInvite(action, relationId) {
  try {
    await App.apiFetch(`/api/couple/${action}`, {
      method: 'POST',
      body: JSON.stringify({ relationId: Number(relationId) }),
    });
    App.showToast('操作成功');
    await loadStatus();
    await loadAllLists();
  } catch (err) {
    App.showToast(err.message);
  }
}

inviteForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(inviteForm).entries());
  try {
    await App.apiFetch('/api/couple/invite', {
      method: 'POST',
      body: JSON.stringify({ targetUserId: Number(payload.targetUserId) }),
    });
    App.showToast('邀请已发送');
    inviteForm.reset();
    await loadStatus();
  } catch (err) {
    App.showToast(err.message);
  }
});

refreshBtn.addEventListener('click', loadStatus);
breakupBtn.addEventListener('click', async () => {
  try {
    await App.apiFetch('/api/couple/breakup', { method: 'POST' });
    App.showToast('已解除关系');
    await loadStatus();
    await loadAllLists();
  } catch (err) {
    App.showToast(err.message);
  }
});

albumCoverInput.addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const result = await uploadImage(file);
    albumForm.coverUrl.value = result.url;
    albumForm.coverDeleteHash.value = result.deleteHash;
    albumCoverResult.textContent = '封面已上传';
  } catch (err) {
    albumCoverResult.textContent = err.message;
  }
});

photoUploadInput.addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const result = await uploadImage(file);
    photoForm.url.value = result.url;
    photoForm.deleteHash.value = result.deleteHash;
    photoUploadResult.textContent = '照片已上传';
  } catch (err) {
    photoUploadResult.textContent = err.message;
  }
});

albumForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(albumForm).entries());
  try {
    await App.apiFetch('/api/couple/albums', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    App.showToast('相册已创建');
    albumForm.reset();
    albumCoverResult.textContent = '';
    loadAlbums();
  } catch (err) {
    App.showToast(err.message);
  }
});

photoForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(photoForm).entries());
  payload.albumId = Number(payload.albumId);
  try {
    await App.apiFetch('/api/couple/albums/photos', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    App.showToast('照片已添加');
    photoForm.reset();
    photoUploadResult.textContent = '';
  } catch (err) {
    App.showToast(err.message);
  }
});

calendarForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(calendarForm).entries());
  payload.shared = payload.shared === 'true';
  try {
    await App.apiFetch('/api/couple/calendar', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    calendarForm.reset();
    loadCalendar();
  } catch (err) {
    App.showToast(err.message);
  }
});

todoForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(todoForm).entries());
  try {
    await App.apiFetch('/api/couple/todos', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    todoForm.reset();
    loadTodos();
  } catch (err) {
    App.showToast(err.message);
  }
});

messageForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(messageForm).entries());
  try {
    await App.apiFetch('/api/couple/messages', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    messageForm.reset();
    loadMessages();
  } catch (err) {
    App.showToast(err.message);
  }
});

milestoneForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(milestoneForm).entries());
  try {
    await App.apiFetch('/api/couple/milestones', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    milestoneForm.reset();
    loadMilestones();
  } catch (err) {
    App.showToast(err.message);
  }
});

dateForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(dateForm).entries());
  payload.remindDays = payload.remindDays ? Number(payload.remindDays) : null;
  try {
    await App.apiFetch('/api/couple/important-dates', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    dateForm.reset();
    loadDates();
  } catch (err) {
    App.showToast(err.message);
  }
});

async function loadAlbums() {
  try {
    const data = await App.apiFetch('/api/couple/albums');
    albumListEl.innerHTML = data
      .map(
        (item) => `
          <div class="list-item">
            <strong>${item.name}</strong>
            <div class="muted">ID ${item.id}</div>
            <button class="button ghost" data-remove="${item.id}">删除</button>
          </div>
        `
      )
      .join('') || '<div class="muted">暂无相册</div>';

    albumListEl.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/albums/${btn.dataset.remove}`, { method: 'DELETE' });
        App.showToast('已删除相册');
        loadAlbums();
      });
    });
  } catch (err) {
    albumListEl.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadCalendar() {
  try {
    const data = await App.apiFetch('/api/couple/calendar');
    calendarList.innerHTML = data
      .map(
        (item) => `
        <div class="list-item">
          <strong>${item.title}</strong>
          <div class="muted">${App.formatDate(item.startTime)}</div>
          <button class="button ghost" data-remove="${item.id}">删除</button>
        </div>
      `
      )
      .join('') || '<div class="muted">暂无日程</div>';
    calendarList.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/calendar/${btn.dataset.remove}`, { method: 'DELETE' });
        loadCalendar();
      });
    });
  } catch (err) {
    calendarList.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadTodos() {
  try {
    const data = await App.apiFetch('/api/couple/todos');
    todoList.innerHTML = data
      .map(
        (item) => `
        <div class="list-item">
          <strong>${item.content}</strong>
          <div class="muted">${item.completed ? '已完成' : '未完成'}</div>
          <div style="margin-top: 8px; display: flex; gap: 8px;">
            <button class="button secondary" data-toggle="${item.id}">切换</button>
            <button class="button ghost" data-remove="${item.id}">删除</button>
          </div>
        </div>
      `
      )
      .join('') || '<div class="muted">暂无待办</div>';
    todoList.querySelectorAll('[data-toggle]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/todos/${btn.dataset.toggle}/toggle`, { method: 'POST' });
        loadTodos();
      });
    });
    todoList.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/todos/${btn.dataset.remove}`, { method: 'DELETE' });
        loadTodos();
      });
    });
  } catch (err) {
    todoList.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadMessages() {
  try {
    const data = await App.apiFetch('/api/couple/messages');
    messageList.innerHTML = data
      .map(
        (item) => `
        <div class="list-item">
          <strong>${item.content}</strong>
          <div class="muted">${App.formatDate(item.createTime)}</div>
          <button class="button ghost" data-remove="${item.id}">删除</button>
        </div>
      `
      )
      .join('') || '<div class="muted">暂无留言</div>';
    messageList.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/messages/${btn.dataset.remove}`, { method: 'DELETE' });
        loadMessages();
      });
    });
  } catch (err) {
    messageList.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadMilestones() {
  try {
    const data = await App.apiFetch('/api/couple/milestones');
    milestoneList.innerHTML = data
      .map(
        (item) => `
        <div class="list-item">
          <strong>${item.title}</strong>
          <div class="muted">${item.eventDate}</div>
          <button class="button ghost" data-remove="${item.id}">删除</button>
        </div>
      `
      )
      .join('') || '<div class="muted">暂无里程碑</div>';
    milestoneList.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/milestones/${btn.dataset.remove}`, { method: 'DELETE' });
        loadMilestones();
      });
    });
  } catch (err) {
    milestoneList.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadDates() {
  try {
    const data = await App.apiFetch('/api/couple/important-dates');
    dateList.innerHTML = data
      .map(
        (item) => `
        <div class="list-item">
          <strong>${item.title}</strong>
          <div class="muted">${item.date} 提前 ${item.remindDays || 0} 天提醒</div>
          <button class="button ghost" data-remove="${item.id}">删除</button>
        </div>
      `
      )
      .join('') || '<div class="muted">暂无重要日期</div>';
    dateList.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        await App.apiFetch(`/api/couple/important-dates/${btn.dataset.remove}`, { method: 'DELETE' });
        loadDates();
      });
    });
  } catch (err) {
    dateList.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadAllLists() {
  await Promise.all([loadAlbums(), loadCalendar(), loadTodos(), loadMessages(), loadMilestones(), loadDates()]);
}

loadStatus();
loadAllLists();
