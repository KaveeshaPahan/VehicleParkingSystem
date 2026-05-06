// Shared API helpers, toast notifications, and modal/form utilities.

const API = {
  base: '',
  async req(method, url, body) {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body !== undefined) opts.body = JSON.stringify(body);
    const res = await fetch(this.base + url, opts);
    if (!res.ok) {
      let detail = '';
      try { detail = await res.text(); } catch (e) {}
      throw new Error(`${res.status} ${res.statusText} ${detail}`);
    }
    if (res.status === 204) return null;
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? res.json() : res.text();
  },
  get(url)        { return this.req('GET', url); },
  post(url, b)    { return this.req('POST', url, b); },
  put(url, b)     { return this.req('PUT', url, b); },
  del(url)        { return this.req('DELETE', url); },
};

function toast(msg, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = msg;
  container.appendChild(el);
  setTimeout(() => {
    el.style.animation = 'slideIn 0.3s reverse';
    setTimeout(() => el.remove(), 300);
  }, 3000);
}

function openModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.add('show');
}
function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.remove('show');
}

// Close modal on backdrop click
document.addEventListener('click', (e) => {
  if (e.target.classList && e.target.classList.contains('modal-backdrop')) {
    e.target.classList.remove('show');
  }
});

// Set active nav based on current page
document.addEventListener('DOMContentLoaded', () => {
  const path = location.pathname.replace(/\/$/, '') || '/index.html';
  document.querySelectorAll('.nav a').forEach((a) => {
    const href = a.getAttribute('href');
    if (href && (path === href || path.endsWith(href))) a.classList.add('active');
  });
});

function fmtDateTime(s) {
  if (!s) return '';
  const d = new Date(s);
  if (isNaN(d.getTime())) return s;
  return d.toLocaleString();
}
function fmtMoney(n) {
  const v = Number(n) || 0;
  return 'Rs. ' + v.toFixed(2);
}
function badgeFor(status) {
  if (!status) return '';
  const s = status.toUpperCase();
  if (['AVAILABLE','CONFIRMED','RESOLVED','COMPLETED','ADMIN'].includes(s)) return 'badge-success';
  if (['OCCUPIED','PENDING','REVIEWED'].includes(s)) return 'badge-warning';
  if (['CANCELLED','MAINTENANCE'].includes(s)) return 'badge-danger';
  if (['CUSTOMER','OPEN'].includes(s)) return 'badge-info';
  return 'badge-primary';
}
function escapeHtml(s) {
  if (s === null || s === undefined) return '';
  return String(s)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}
