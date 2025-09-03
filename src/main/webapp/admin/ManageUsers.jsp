<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Hotel Admin - User Management</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
  <style>
    .container {
      display: flex;
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
      gap: 2rem;
    }

    .admin-menu {
      width: 250px;
      background-color: var(--card-bg);
      padding: 1.5rem;
      border-radius: 8px;
      border: 1px solid #333;
    }

    .admin-menu h2 {
      color: var(--text-primary);
      margin-bottom: 1.5rem;
      font-size: 1.5rem;
    }

    .admin-menu a {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: var(--text-secondary);
      text-decoration: none;
      padding: 0.75rem;
      border-radius: 6px;
      margin-bottom: 0.5rem;
      transition: background-color 0.3s;
    }

    .admin-menu a:hover, .admin-menu a.active {
      background-color: var(--primary);
      color: #000;
    }

    .admin-content {
      flex-grow: 1;
      background-color: var(--card-bg);
      padding: 1.5rem;
      border-radius: 8px;
      border: 1px solid #333;
    }

    .header h1 {
      color: var(--text-primary);
      margin: 0 0 1.5rem 0;
    }

    .toolbar {
      margin-bottom: 1.5rem;
    }

    .toolbar button {
      background-color: var(--primary);
      color: #000;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: background-color 0.3s;
    }

    .toolbar button:hover {
      background-color: #9965f4;
    }

    .table-container {
      overflow-x: auto;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      color: var(--text-primary);
    }

    th, td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #333;
    }

    th {
      background-color: #2a2a2a;
      color: var(--text-secondary);
    }

    .text-muted {
      color: var(--text-secondary);
    }

    .actions button {
      margin-right: 0.5rem;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      border: none;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .btn-edit {
      background-color: var(--secondary);
      color: #000;
    }

    .btn-edit:hover {
      background-color: #02b3a1;
    }

    .btn-delete {
      background-color: #dc3545;
      color: #fff;
    }

    .btn-delete:hover {
      background-color: #c82333;
    }

    /* Bootstrap Modal Customizations */
    .modal-content {
      background-color: var(--card-bg);
      color: var(--text-primary);
      border: 1px solid #333;
      border-radius: 12px;
    }

    .modal-header {
      border-bottom: 1px solid #333;
      padding: 1rem 1.5rem;
    }

    .modal-header .btn-close {
      filter: invert(1);
    }

    .modal-title {
      color: var(--text-primary);
    }

    .modal-body {
      padding: 1.5rem;
    }

    .modal-footer {
      border-top: 1px solid #333;
      padding: 1rem 1.5rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-group label {
      display: block;
      color: var(--text-secondary);
      margin-bottom: 0.5rem;
    }

    .form-group input,
    .form-group select {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #444;
      border-radius: 6px;
      background-color: #2a2a2a;
      color: var(--text-primary);
    }

    .form-group input:focus,
    .form-group select:focus {
      border-color: var(--primary);
      box-shadow: 0 0 0 0.25rem rgba(187, 134, 252, 0.25);
      outline: none;
    }

    .btn-primary {
      background-color: var(--primary);
      color: #000;
      border: none;
    }

    .btn-primary:hover {
      background-color: #9965f4;
    }

    .btn-secondary {
      background-color: #2a2a2a;
      color: var(--text-primary);
      border: none;
    }

    .btn-secondary:hover {
      background-color: #3a3a3a;
    }

    .btn-danger {
      background-color: #dc3545;
      color: #fff;
      border: none;
    }

    .btn-danger:hover {
      background-color: #c82333;
    }
  </style>
</head>
<body>
<div class="container">
  <!-- Sidebar Navigation -->
  <jsp:include page="includes/admin_nav_bar.jsp">
    <jsp:param name="page" value="manageUsers"/>
  </jsp:include>

  <!-- Main Content Area -->
  <main class="admin-content">
    <div class="header">
      <h1>User Management</h1>
    </div>

    <div class="toolbar">
      <div class="filter-options">
        <button class="btn" onclick="openAddUserModal()">
          <i class="fas fa-plus"></i> Add User
        </button>
      </div>
    </div>

    <!-- Users Table -->
    <div class="table-container">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>User</th>
          <th>Email</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>

        <c:forEach var="user" items="${requestScope.allUsers}">
          <tr>
            <td>${user.userId}</td>
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.isAdmin() eq true ? "Admin" : "User"}</td>

            <td class="actions">
              <button class="btn btn-edit" onclick="openEditModal('${user.userId}', '${user.firstName}', '${user.lastName}', '${user.email}'2)">
                <i class="fas fa-edit"></i>Edit
              </button>
              <button class="btn btn-delete" onclick="openDeleteModal(<%=user.getUserId()%>, '<%=user.getFirstName()%> <%=user.getLastName()%>')">
                <i class="fas fa-trash"></i>Delete
              </button>
            </td>
          </tr>
        </c:forEach>

        </tbody>
      </table>
    </div>
  </main>
</div>

<!-- Add User Modal -->
<jsp:include page="includes/modals/add_user_modal.jsp" />

<!-- Edit User Modal -->
<jsp:include page="includes/modals/edit_user_modal.jsp" />

<!-- Delete User Modal -->
<jsp:include page="includes/modals/delete_user_modal.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Initialize Bootstrap modals
  const addUserModal = new bootstrap.Modal(document.getElementById('addUserModal'));
  const editUserModal = new bootstrap.Modal(document.getElementById('editUserModal'));
  const deleteUserModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));

  function openAddUserModal() {
    addUserModal.show();
  }

  function closeAddUserModal() {
    addUserModal.hide();
  }

  function openEditModal(userId, firstName, lastName, email, phone, password) {
    document.getElementById('editUserId').value = userId;
    document.getElementById('editFirstName').value = firstName;
    document.getElementById('editLastName').value = lastName;
    document.getElementById('editEmail').value = email;
    document.getElementById('editPhone').value = phone;
    document.getElementById('editPassword').value = password;
    editUserModal.show();
  }

  function closeEditModal() {
    editUserModal.hide();
  }

  function openDeleteModal(userId, userName) {
    document.getElementById('deleteUserId').value = userId;
    document.getElementById('deleteUserName').textContent = userName;
    deleteUserModal.show();
  }

  function closeDeleteModal() {
    deleteUserModal.hide();
  }
</script>
</body>
</html>