App.requireAuth();
App.initNav();

const listEl = document.getElementById('article-list');
const searchInput = document.getElementById('search-input');
const searchBtn = document.getElementById('search-btn');
const sidebarProfile = document.getElementById('sidebar-profile');
const postForm = document.getElementById('post-form');
const previewEl = document.getElementById('markdown-preview');

function renderMarkdown(text = '') {
  if (window.marked) {
    return window.marked.parse(text);
  }
  return `<pre>${text}</pre>`;
}

async function loadArticles(query = '') {
  listEl.innerHTML = '<div class="muted">加载中...</div>';
  try {
    const data = await App.apiFetch(`/api/home/articles?page=1&size=10&q=${encodeURIComponent(query)}`);
    const records = data.records || [];
    if (!records.length) {
      listEl.innerHTML = '<div class="muted">暂无文章</div>';
      return;
    }
    listEl.innerHTML = records
      .map(
        (item) => `
        <article class="list-item">
          <h3>${item.title || '未命名文章'}</h3>
          <p class="muted">${item.summary || '暂无摘要'}</p>
          <div class="muted" style="margin-top: 8px;">发布时间：${App.formatDate(item.createTime)}</div>
          <details style="margin-top: 12px;">
            <summary class="muted">查看正文</summary>
            <div class="markdown-body">${renderMarkdown(item.content || item.contentHtml || '')}</div>
          </details>
        </article>
      `
      )
      .join('');
  } catch (err) {
    listEl.innerHTML = `<div class="muted">${err.message}</div>`;
  }
}

async function loadProfile() {
  try {
    const data = await App.apiFetch('/api/home/admin-profile');
    const items = [
      { label: '昵称', value: data.nickname },
      { label: '公告', value: data.notice },
      { label: 'GitHub', value: data.github },
      { label: 'Bilibili', value: data.bilibili },
      { label: '邮箱', value: data.email },
    ];
    sidebarProfile.innerHTML = items
      .filter((item) => item.value)
      .map(
        (item) => `<div class="list-item"><strong>${item.label}</strong><div class="muted">${item.value}</div></div>`
      )
      .join('');
  } catch (err) {
    sidebarProfile.innerHTML = '<div class="muted">站长信息加载失败</div>';
  }
}

postForm.content.addEventListener('input', (event) => {
  previewEl.innerHTML = renderMarkdown(event.target.value);
});

postForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(postForm).entries());
  try {
    await App.apiFetch('/api/articles', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    App.showToast('文章已发布');
    postForm.reset();
    previewEl.innerHTML = '';
    loadArticles();
  } catch (err) {
    App.showToast(err.message);
  }
});

searchBtn.addEventListener('click', () => loadArticles(searchInput.value.trim()));

previewEl.innerHTML = renderMarkdown('**Markdown** 内容预览');
loadArticles();
loadProfile();
