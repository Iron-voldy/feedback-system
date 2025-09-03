<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hotel Admin Dashboard</title>
    <link rel="stylesheet" href="../css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }

        header {
            background-color: var(--card-bg);
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            text-align: center;
            border: 1px solid #333;
        }

        header h1 {
            color: var(--text-primary);
            margin: 0;
            font-size: 2rem;
        }

        .welcome-message {
            background-color: var(--card-bg);
            color: var(--text-secondary);
            padding: 1rem;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 2rem;
            border: 1px solid #333;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
        }

        .dashboard-card {
            background-color: var(--card-bg);
            border-radius: 8px;
            padding: 1.5rem;
            text-align: center;
            transition: transform 0.3s, box-shadow 0.3s;
            border: 1px solid #333;
        }

        .dashboard-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.3);
        }

        .dashboard-card a {
            text-decoration: none;
            color: var(--text-primary);
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.5rem;
        }

        .dashboard-card i {
            font-size: 2rem;
            color: var(--primary);
        }

        .card-title {
            font-size: 1.2rem;
            font-weight: 600;
        }

        footer {
            background-color: #1a1a1a;
            color: var(--text-secondary);
            text-align: center;
            padding: 1rem;
            margin-top: 2rem;
            border-radius: 8px;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>Hotel Management Admin Dashboard</h1>
    </header>

    <div class="welcome-message">
        Welcome back, Administrator!.
    </div>

    <div class="dashboard-grid">
        <!-- User Management Card -->
        <div class="dashboard-card card-user">
            <a href="<%=request.getContextPath()%>/admin/ManageUsers.jsp">
                <i class="fas fa-users-cog"></i>
                <span class="card-title">User Management</span>
            </a>
        </div>

<%--        <!-- Room Management Card -->--%>
<%--        <div class="dashboard-card card-room">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManageRooms.jsp">--%>
<%--                <i class="fas fa-bed"></i>--%>
<%--                <span class="card-title">Room Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

<%--        <!-- Booking Management Card -->--%>
<%--        <div class="dashboard-card card-booking">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManageBookings.jsp">--%>
<%--                <i class="fas fa-calendar-check"></i>--%>
<%--                <span class="card-title">Booking Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

<%--        <!-- Payment Management Card -->--%>
<%--        <div class="dashboard-card card-payment">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManagePayments.jsp">--%>
<%--                <i class="fas fa-credit-card"></i>--%>
<%--                <span class="card-title">Payment Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

<%--        <!-- Feedback Management Card -->--%>
<%--        <div class="dashboard-card card-feedback">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManageFeedback.jsp">--%>
<%--                <i class="fas fa-comment-alt"></i>--%>
<%--                <span class="card-title">Feedback Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

<%--        <!-- Services Management Card -->--%>
<%--        <div class="dashboard-card card-reports">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManageServices.jsp">--%>
<%--                <i class="fa-solid fa-bell-concierge"></i>--%>
<%--                <span class="card-title">Services Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

<%--        <!-- Service Requests Management Card -->--%>
<%--        <div class="dashboard-card card-service-requests">--%>
<%--            <a href="<%=request.getContextPath()%>/admin/ManageServiceRequests.jsp">--%>
<%--                <i class="fa-solid fa-reply-all"></i>--%>
<%--                <span class="card-title">Service Requests Management</span>--%>
<%--            </a>--%>
<%--        </div>--%>

        <!-- Logout Card -->
        <div class="dashboard-card card-logout">
            <a href="<%=request.getContextPath()%>/LogoutServlet">
                <i class="fas fa-sign-out-alt"></i>
                <span class="card-title">Logout</span>
            </a>
        </div>
    </div>

    <footer>
        &copy; 2023 Hotel Management System | Admin Panel
    </footer>
</div>
</body>
</html>