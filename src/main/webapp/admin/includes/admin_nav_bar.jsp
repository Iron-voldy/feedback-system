

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:set var="page" value="${param.page}" />

<nav class="admin-menu">
  <h2>Hotel Admin</h2>
  <a href="${pageContext.request.contextPath}/admin/index.jsp"><i class="fas fa-tachometer-alt"></i> Dashboard</a>
  <a href="${pageContext.request.contextPath}/admin/ManageUsers.jsp" class="${page eq 'manageUsers' ? 'active' : ''}"><i class="fas fa-users-cog"></i> User Management</a>
  <a href="${pageContext.request.contextPath}/LogoutServlet"><i class="fas fa-sign-out-alt"></i> Logout</a>
</nav>