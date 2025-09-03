<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Feedback Management - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feedback-support.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<header>
    <div class="wrap" style="display:flex;align-items:center;justify-content:space-between;gap:16px">
        <div>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/">Admin</a>
                <span>></span>
                <span>Feedback Management</span>
            </div>
            <h1>Feedback & Support Dashboard</h1>
        </div>
        <div class="stack">
            <a href="${pageContext.request.contextPath}/FeedbackServlet?action=feedbackStats" class="btn ghost">
                <i class="fas fa-chart-bar"></i> Statistics
            </a>
            <a href="${pageContext.request.contextPath}/admin/" class="btn ghost">
                <i class="fas fa-arrow-left"></i> Back to Admin
            </a>
        </div>
    </div>
</header>

<main class="wrap" style="margin-top:16px">

    <!-- Success/Error Messages -->
    <c:if test="${not empty requestScope.success}">
        <div class="alert success">
            <i class="fas fa-check-circle"></i> ${requestScope.success}
        </div>
    </c:if>

    <c:if test="${not empty requestScope.error}">
        <div class="alert error">
            <i class="fas fa-exclamation-circle"></i> ${requestScope.error}
        </div>
    </c:if>

    <!-- Dashboard Section -->
    <section class="card">
        <div class="tabs">
            <button class="tab active" data-tab="feedback" onclick="showTab('feedback')">
                <i class="fas fa-comments"></i> All Feedback
            </button>
            <button class="tab" data-tab="tickets" onclick="showTab('tickets')">
                <i class="fas fa-ticket-alt"></i> Support Tickets
            </button>
            <button class="tab" data-tab="reviews" onclick="showTab('reviews')">
                <i class="fas fa-star"></i> Reviews
            </button>
        </div>

        <div class="body">
            <!-- KPI Cards -->
            <div class="kpis">
                <div class="kpi">
                    <h3><i class="fas fa-comments"></i> Total Feedback</h3>
                    <p>${requestScope.totalFeedback != null ? requestScope.totalFeedback : 0}</p>
                </div>
                <div class="kpi">
                    <h3><i class="fas fa-envelope"></i> Unread</h3>
                    <p>${requestScope.unreadCount != null ? requestScope.unreadCount : 0}</p>
                </div>
                <div class="kpi">
                    <h3><i class="fas fa-star"></i> Avg Rating</h3>
                    <p>${requestScope.averageRating != null ? requestScope.averageRating : '--'}%</p>
                </div>
                <div class="kpi">
                    <h3><i class="fas fa-bug"></i> Bug Reports</h3>
                    <p>${requestScope.bugReports != null ? requestScope.bugReports : 0}</p>
                </div>
            </div>

            <!-- Feedback Tab -->
            <div id="tab-feedback" class="tab-panel">
                <div class="stack" style="justify-content:space-between;margin:14px 0">
                    <div class="stack">
                        <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminAllFeedback" class="btn ghost">All</a>
                        <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminUnreadFeedback" class="btn ghost">Unread</a>
                        <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminFeedbackByType&type=bug_report" class="btn ghost">Bug Reports</a>
                        <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminFeedbackByType&type=feature_request" class="btn ghost">Feature Requests</a>
                        <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminFeedbackByType&type=general" class="btn ghost">General</a>
                    </div>
                </div>

                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th style="width:80px">Status</th>
                            <th style="width:100px">Type</th>
                            <th>Subject</th>
                            <th style="width:120px">User</th>
                            <th style="width:80px">Rating</th>
                            <th style="width:130px">Submitted</th>
                            <th style="width:150px">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty requestScope.allFeedback}">
                                <c:forEach var="feedback" items="${requestScope.allFeedback}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${feedback.read}">
                                                    <span class="pill done"><i class="fas fa-check"></i> Read</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="pill open"><i class="fas fa-envelope"></i> New</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${feedback.feedbackType eq 'bug_report'}">
                                                    <span class="pill" style="background:rgba(239,68,68,0.15);color:#fecaca;">
                                                        <i class="fas fa-bug"></i> Bug
                                                    </span>
                                                </c:when>
                                                <c:when test="${feedback.feedbackType eq 'feature_request'}">
                                                    <span class="pill" style="background:rgba(245,158,11,0.16);color:#ffdd9a;">
                                                        <i class="fas fa-lightbulb"></i> Feature
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="pill" style="background:rgba(99,102,241,0.15);color:#c7c9ff;">
                                                        <i class="fas fa-comment"></i> General
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="font-weight:600;">${feedback.subject}</div>
                                            <div class="muted" style="font-size:12px;">
                                                ${fn:substring(feedback.message, 0, 80)}${fn:length(feedback.message) > 80 ? '...' : ''}
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${feedback.anonymous}">
                                                    <span class="muted">Anonymous</span><br>
                                                    <small>${feedback.email}</small>
                                                </c:when>
                                                <c:otherwise>
                                                    ${feedback.userName}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${feedback.rating > 0}">
                                                    <div style="display:flex;gap:2px;">
                                                        <c:forEach var="i" begin="1" end="${feedback.rating}">
                                                            <i class="fas fa-star" style="color:#fbbf24;font-size:10px;"></i>
                                                        </c:forEach>
                                                        <c:forEach var="i" begin="${feedback.rating + 1}" end="5">
                                                            <i class="far fa-star" style="color:#475569;font-size:10px;"></i>
                                                        </c:forEach>
                                                    </div>
                                                    <small class="muted">${feedback.rating}/5</small>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="muted">No rating</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${feedback.submittedDate}" pattern="MMM dd, yyyy" var="dateStr"/>
                                            <fmt:formatDate value="${feedback.submittedDate}" pattern="HH:mm" var="timeStr"/>
                                            <div>${dateStr}</div>
                                            <small class="muted">${timeStr}</small>
                                        </td>
                                        <td>
                                            <div class="stack">
                                                <a href="${pageContext.request.contextPath}/FeedbackServlet?action=adminFeedbackDetails&feedbackId=${feedback.feedbackId}" class="btn ghost" style="padding:6px 8px;font-size:12px;">
                                                    <i class="fas fa-eye"></i> View
                                                </a>
                                                <c:if test="${not feedback.read}">
                                                    <a href="${pageContext.request.contextPath}/FeedbackServlet?action=markAsRead&feedbackId=${feedback.feedbackId}" class="btn success" style="padding:6px 8px;font-size:12px;">
                                                        <i class="fas fa-check"></i> Mark Read
                                                    </a>
                                                </c:if>
                                                <a href="${pageContext.request.contextPath}/FeedbackServlet?action=deleteFeedback&feedbackId=${feedback.feedbackId}"
                                                   class="btn danger"
                                                   style="padding:6px 8px;font-size:12px;"
                                                   onclick="return confirm('Are you sure you want to delete this feedback?')">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" style="text-align:center;padding:40px;color:var(--muted);">
                                        <i class="fas fa-comment" style="font-size:48px;margin-bottom:16px;display:block;opacity:0.3;"></i>
                                        No feedback found
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Support Tickets Tab -->
            <div id="tab-tickets" class="tab-panel" style="display:none">
                <div class="stack" style="justify-content:space-between;margin:14px 0">
                    <div class="stack">
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminAllTickets" class="btn ghost">All Tickets</a>
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminTicketsByStatus&status=open" class="btn ghost">Open</a>
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminTicketsByStatus&status=in_progress" class="btn ghost">In Progress</a>
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminTicketsByStatus&status=resolved" class="btn ghost">Resolved</a>
                    </div>
                </div>

                <div style="text-align:center;padding:40px;color:var(--muted);">
                    <i class="fas fa-ticket-alt" style="font-size:48px;margin-bottom:16px;display:block;opacity:0.3;"></i>
                    <p>Navigate to the full ticket management system</p>
                    <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminAllTickets" class="btn primary">
                        <i class="fas fa-external-link-alt"></i> Manage All Tickets
                    </a>
                </div>
            </div>

            <!-- Reviews Tab -->
            <div id="tab-reviews" class="tab-panel" style="display:none">
                <div class="stack" style="justify-content:space-between;margin:14px 0">
                    <div class="stack">
                        <a href="${pageContext.request.contextPath}/ReviewServlet?action=adminPendingReviews" class="btn ghost">Pending Reviews</a>
                    </div>
                </div>

                <div style="text-align:center;padding:40px;color:var(--muted);">
                    <i class="fas fa-star" style="font-size:48px;margin-bottom:16px;display:block;opacity:0.3;"></i>
                    <p>Navigate to the review management system</p>
                    <a href="${pageContext.request.contextPath}/ReviewServlet?action=adminPendingReviews" class="btn primary">
                        <i class="fas fa-external-link-alt"></i> Manage Reviews
                    </a>
                </div>
            </div>
        </div>
    </section>

</main>

<script>
    // Tab switching functionality
    function showTab(tabName) {
        // Remove active class from all tabs
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.remove('active');
        });

        // Hide all tab panels
        document.querySelectorAll('.tab-panel').forEach(panel => {
            panel.style.display = 'none';
        });

        // Show selected tab
        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).style.display = 'block';
    }

    // Auto-refresh unread count every 30 seconds
    setInterval(function() {
        // You could implement AJAX call here to update unread count
        // fetch('/FeedbackServlet?action=getUnreadCount')...
    }, 30000);
</script>

</body>
</html>