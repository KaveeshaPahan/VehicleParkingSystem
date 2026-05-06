// Client-side auth + role-based access helpers.

const AUTH_KEY = 'vms_user';

function getCurrentUser() {
  try { return JSON.parse(localStorage.getItem(AUTH_KEY)); }
  catch (e) { return null; }
}

function setCurrentUser(user) {
  if (user) localStorage.setItem(AUTH_KEY, JSON.stringify(user));
  else localStorage.removeItem(AUTH_KEY);
}

function logout() {
  setCurrentUser(null);
  location.href = '/landing.html';
}

function getRole() {
  const u = getCurrentUser();
  return u ? (u.role || '').toUpperCase() : null;
}

function isAdmin() { return getRole() === 'ADMIN'; }
function isCustomer() { return getRole() === 'CUSTOMER'; }

/** Send to login page if no user is logged in. */
function requireAuth() {
  const u = getCurrentUser();
  if (!u) { location.href = '/login.html'; return null; }
  return u;
}

/** Restrict a page to admins only. Customers are bounced back to /dashboard. */
function requireAdmin() {
  const u = requireAuth();
  if (!u) return null;
  if ((u.role || '').toUpperCase() !== 'ADMIN') {
    alert('Admin access required.');
    location.href = '/dashboard';
    return null;
  }
  return u;
}

/**
 * Hide elements that don't apply to the current role.
 * Mark elements with `data-admin-only` or `data-customer-only`.
 */
function applyRoleGuards() {
  const admin = isAdmin();
  document.querySelectorAll('[data-admin-only]').forEach(el => {
    if (!admin) el.style.display = 'none';
  });
  document.querySelectorAll('[data-customer-only]').forEach(el => {
    if (admin) el.style.display = 'none';
  });
}

/** Render a top header bar with user info + logout. */
function renderTopbar(currentPage) {
  const user = getCurrentUser();
  if (!user) return;
  const main = document.querySelector('.main');
  if (!main) return;
  const bar = document.createElement('div');
  bar.className = 'topbar';
  bar.innerHTML = `
    <div class="topbar-left">
      <span class="topbar-page">${currentPage || ''}</span>
    </div>
    <div class="topbar-right">
      <div class="topbar-user">
        <div class="topbar-avatar">${(user.name || '?').charAt(0).toUpperCase()}</div>
        <div>
          <div class="topbar-name">${user.name || ''}</div>
          <div class="topbar-role">${user.role || ''}</div>
        </div>
      </div>
      <button class="btn btn-secondary btn-sm" onclick="logout()">⏻ Logout</button>
    </div>`;
  main.insertBefore(bar, main.firstChild);
  // Run guards after the DOM is in place
  applyRoleGuards();
}
