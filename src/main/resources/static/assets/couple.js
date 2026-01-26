App.requireAuth();
App.initNav();

const statusEl = document.getElementById('relation-status');
const pendingEl = document.getElementById('pending-invites');
const inviteForm = document.getElementById('invite-form');
const refreshBtn = document.getElementById('refresh-status');
const breakupBtn = document.getElementById('breakup-btn');
const tabs = document.querySelectorAll('#content-tabs .tab');
const panel = document.getElementById('content-panel');

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
  } catch (err) {
    App.showToast(err.message);
  }
});

const templates = {
  albums: () => `
    <div class="grid grid-2">
      <form class="form" id="album-form">
        <label>相册名称</label>
        <input class="input" name="name" required />
        <label>封面图片 URL</label>
        <input class="input" name="coverUrl" />
        <label>封面 deleteHash</label>
        <input class="input" name="coverDeleteHash" />
        <label>描述</label>
        <input class="input" name="description" />
        <button class="button" type="submit">创建相册</button>
      </form>
      <div>
        <h4>相册列表</h4>
        <div id="album-list" class="list"></div>
      </div>
    </div>
    <div class="card" style="margin-top: 16px;">
      <h4>上传照片到相册</h4>
      <form class="form" id="photo-form">
        <label>相册 ID</label>
        <input class="input" name="albumId" required />
        <label>图片 URL</label>
        <input class="input" name="url" required />
        <label>deleteHash</label>
        <input class="input" name="deleteHash" />
        <label>备注</label>
        <input class="input" name="note" />
        <button class="button" type="submit">添加照片</button>
      </form>
      <div style="margin-top: 12px;">
        <label>或直接上传图片（SM.MS）</label>
        <input class="input" type="file" id="photo-upload" accept="image/*" />
        <div class="muted" id="upload-result"></div>
      </div>
    </div>
  `,
  calendar: () => `
    <div class="grid grid-2">
      <form class="form" id="calendar-form">
        <label>标题</label>
        <input class="input" name="title" required />
        <label>描述</label>
        <input class="input" name="description" />
        <label>开始时间</label>
        <input class="input" name="startTime" type="datetime-local" required />
        <label>结束时间</label>
        <input class="input" name="endTime" type="datetime-local" />
        <label>共享</label>
        <select class="input" name="shared">
          <option value="true">共享</option>
          <option value="false">仅自己</option>
        </select>
        <button class="button" type="submit">创建日程</button>
      </form>
      <div>
        <h4>日程列表</h4>
        <div id="calendar-list" class="list"></div>
      </div>
    </div>
  `,
  todos: () => `
    <div class="grid grid-2">
      <form class="form" id="todo-form">
        <label>待办内容</label>
        <input class="input" name="content" required />
        <label>截止时间</label>
        <input class="input" name="dueTime" type="datetime-local" />
        <button class="button" type="submit">创建待办</button>
      </form>
      <div>
        <h4>待办列表</h4>
        <div id="todo-list" class="list"></div>
      </div>
    </div>
  `,
  messages: () => `
    <div class="grid grid-2">
      <form class="form" id="message-form">
        <label>留言内容</label>
        <textarea class="input" name="content" required style="min-height: 120px;"></textarea>
        <button class="button" type="submit">发送留言</button>
      </form>
      <div>
        <h4>留言列表</h4>
        <div id="message-list" class="list"></div>
      </div>
    </div>
  `,
  milestones: () => `
    <div class="grid grid-2">
      <form class="form" id="milestone-form">
        <label>里程碑标题</label>
        <input class="input" name="title" required />
        <label>描述</label>
        <input class="input" name="description" />
        <label>日期</label>
        <input class="input" name="eventDate" type="date" required />
        <button class="button" type="submit">创建里程碑</button>
      </form>
      <div>
        <h4>里程碑列表</h4>
        <div id="milestone-list" class="list"></div>
      </div>
    </div>
  `,
  dates: () => `
    <div class="grid grid-2">
      <form class="form" id="date-form">
        <label>重要日期名称</label>
        <input class="input" name="title" required />
        <label>日期</label>
        <input class="input" name="date" type="date" required />
        <label>提前提醒天数</label>
        <input class="input" name="remindDays" type="number" />
        <button class="button" type="submit">添加日期</button>
      </form>
      <div>
        <h4>重要日期列表</h4>
        <div id="date-list" class="list"></div>
      </div>
    </div>
  `,
};

function setActiveTab(name) {
  tabs.forEach((tab) => tab.classList.toggle('active', tab.dataset.tab === name));
  panel.innerHTML = templates[name]();
  initTabActions(name);
}

async function initTabActions(name) {
  if (name === 'albums') {
    const listEl = document.getElementById('album-list');
    const form = document.getElementById('album-form');
    const photoForm = document.getElementById('photo-form');
    const uploadInput = document.getElementById('photo-upload');
    const uploadResult = document.getElementById('upload-result');

    async function loadAlbums() {
      const data = await App.apiFetch('/api/couple/albums');
      listEl.innerHTML = data
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

      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/albums/${btn.dataset.remove}`, { method: 'DELETE' });
          App.showToast('已删除相册');
          loadAlbums();
        });
      });
    }

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      await App.apiFetch('/api/couple/albums', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      App.showToast('相册已创建');
      form.reset();
      loadAlbums();
    });

    photoForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(photoForm).entries());
      payload.albumId = Number(payload.albumId);
      await App.apiFetch('/api/couple/albums/photos', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      App.showToast('照片已添加');
      photoForm.reset();
    });

    uploadInput.addEventListener('change', async (event) => {
      const file = event.target.files[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      try {
        const response = await fetch('/api/upload/image', {
          method: 'POST',
          headers: { ...App.getToken() ? { Authorization: `Bearer ${App.getToken()}` } : {} },
          body: formData,
        });
        const data = await response.json();
        if (data.code !== 0) throw new Error(data.message || '上传失败');
        uploadResult.textContent = `URL: ${data.data.url} | deleteHash: ${data.data.deleteHash}`;
      } catch (err) {
        uploadResult.textContent = err.message;
      }
    });

    loadAlbums();
  }

  if (name === 'calendar') {
    const listEl = document.getElementById('calendar-list');
    const form = document.getElementById('calendar-form');
    async function loadCalendar() {
      const data = await App.apiFetch('/api/couple/calendar');
      listEl.innerHTML = data
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
      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/calendar/${btn.dataset.remove}`, { method: 'DELETE' });
          loadCalendar();
        });
      });
    }
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      payload.shared = payload.shared === 'true';
      await App.apiFetch('/api/couple/calendar', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      form.reset();
      loadCalendar();
    });
    loadCalendar();
  }

  if (name === 'todos') {
    const listEl = document.getElementById('todo-list');
    const form = document.getElementById('todo-form');
    async function loadTodos() {
      const data = await App.apiFetch('/api/couple/todos');
      listEl.innerHTML = data
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
      listEl.querySelectorAll('[data-toggle]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/todos/${btn.dataset.toggle}/toggle`, { method: 'POST' });
          loadTodos();
        });
      });
      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/todos/${btn.dataset.remove}`, { method: 'DELETE' });
          loadTodos();
        });
      });
    }
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      await App.apiFetch('/api/couple/todos', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      form.reset();
      loadTodos();
    });
    loadTodos();
  }

  if (name === 'messages') {
    const listEl = document.getElementById('message-list');
    const form = document.getElementById('message-form');
    async function loadMessages() {
      const data = await App.apiFetch('/api/couple/messages');
      listEl.innerHTML = data
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
      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/messages/${btn.dataset.remove}`, { method: 'DELETE' });
          loadMessages();
        });
      });
    }
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      await App.apiFetch('/api/couple/messages', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      form.reset();
      loadMessages();
    });
    loadMessages();
  }

  if (name === 'milestones') {
    const listEl = document.getElementById('milestone-list');
    const form = document.getElementById('milestone-form');
    async function loadMilestones() {
      const data = await App.apiFetch('/api/couple/milestones');
      listEl.innerHTML = data
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
      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/milestones/${btn.dataset.remove}`, { method: 'DELETE' });
          loadMilestones();
        });
      });
    }
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      await App.apiFetch('/api/couple/milestones', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      form.reset();
      loadMilestones();
    });
    loadMilestones();
  }

  if (name === 'dates') {
    const listEl = document.getElementById('date-list');
    const form = document.getElementById('date-form');
    async function loadDates() {
      const data = await App.apiFetch('/api/couple/important-dates');
      listEl.innerHTML = data
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
      listEl.querySelectorAll('[data-remove]').forEach((btn) => {
        btn.addEventListener('click', async () => {
          await App.apiFetch(`/api/couple/important-dates/${btn.dataset.remove}`, { method: 'DELETE' });
          loadDates();
        });
      });
    }
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(form).entries());
      payload.remindDays = payload.remindDays ? Number(payload.remindDays) : null;
      await App.apiFetch('/api/couple/important-dates', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      form.reset();
      loadDates();
    });
    loadDates();
  }
}

tabs.forEach((tab) => tab.addEventListener('click', () => setActiveTab(tab.dataset.tab)));

loadStatus();
setActiveTab('albums');
