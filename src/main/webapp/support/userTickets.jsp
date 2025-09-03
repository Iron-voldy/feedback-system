<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>My Support Tickets - RhythmWave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feedback-support.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<!-- Include navbar -->
<jsp:include page="/includes/navbar.jsp">
    <jsp:param name="page" value="support"/>
</jsp:include>

<header>
    <div class="wrap" style="display:flex;align-items:center;justify-content:space-between;gap:16px">
        <div>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/">Home</a>
                <span>></span>
                <span>My Support Tickets</span>
            </div>
            <h1>My Support Tickets</h1>
        </div>
        <div class="stack">
            <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=showCreateForm" class="btn primary">
                <i class="fas fa-plus"></i> Create New Ticket
            </a>
            <a href="${pageContext.request.contextPath}/FeedbackServlet?action=showFeedbackForm" class="btn ghost">
                <i class="fas fa-comment"></i> Give Feedback
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

    <!-- Tickets List -->
    <section class="card">
        <div class="body">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
                <h2><i class="fas fa-ticket-alt"></i> Your Support Tickets</h2>
                <div class="stack">
                    <button class="btn ghost" onclick="filterTickets('all')" id="filter-all">All</button>
                    <button class="btn ghost" onclick="filterTickets('open')" id="filter-open">Open</button>
                    <button class="btn ghost" onclick="filterTickets('in_progress')" id="filter-progress">In Progress</button>
                    <button class="btn ghost" onclick="filterTickets('resolved')" id="filter-resolved">Resolved</button>
                </div>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th style="width:120px">Ticket ID</th>
                        <th>Subject</th>
                        <th style="width:100px">Type</th>
                        <th style="width:100px">Priority</th>
                        <th style="width:120px">Status</th>
                        <th style="width:130px">Created</th>
                        <th style="width:130px">Last Updated</th>
                        <th style="width:100px">Actions</th>
                    </tr>
                    </thead>
                    <tbody id="ticketsTableBody">
                    <c:choose>
                        <c:when test="${not empty requestScope.userTickets}">
                            <c:forEach var="ticket" items="${requestScope.userTickets}">
                                <tr data-status="${ticket.status}" class="ticket-row">
                                    <td>
                                        <code style="font-weight:bold;">#${ticket.ticketId}</code>
                                    </td>
                                    <td>
                                        <div style="font-weight:600;margin-bottom:4px;">
                                            <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=viewTicket&ticketId=${ticket.ticketId}"
                                               style="color:var(--text);text-decoration:none;">
                                                ${fn:length(ticket.subject) > 50 ? fn:substring(ticket.subject, 0, 50).concat('...') : ticket.subject}
                                            </a>
                                        </div>
                                        <c:if test="${not empty ticket.assignedAdminName}">
                                            <small class="muted">
                                                <i class="fas fa-user-tie"></i> Assigned to: ${ticket.assignedAdminName}
                                            </small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ticket.ticketType eq 'complaint'}">
                                                <span class="pill" style="background:rgba(239,68,68,0.15);color:#fecaca;">
                                                    <i class="fas fa-exclamation-triangle"></i> Complaint
                                                </span>
                                            </c:when>
                                            <c:when test="${ticket.ticketType eq 'refund'}">
                                                <span class="pill" style="background:rgba(245,158,11,0.16);color:#ffdd9a;">
                                                    <i class="fas fa-money-bill-wave"></i> Refund
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pill" style="background:rgba(99,102,241,0.15);color:#c7c9ff;">
                                                    <i class="fas fa-question-circle"></i> General
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ticket.priority eq 'urgent'}">
                                                <span class="pill danger"><i class="fas fa-exclamation"></i> Urgent</span>
                                            </c:when>
                                            <c:when test="${ticket.priority eq 'high'}">
                                                <span class="pill" style="background:rgba(239,68,68,0.15);color:#fecaca;">
                                                    <i class="fas fa-arrow-up"></i> High
                                                </span>
                                            </c:when>
                                            <c:when test="${ticket.priority eq 'medium'}">
                                                <span class="pill progress"><i class="fas fa-minus"></i> Medium</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pill done"><i class="fas fa-arrow-down"></i> Low</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ticket.status eq 'open'}">
                                                <span class="pill open">
                                                    <i class="fas fa-circle"></i> Open
                                                </span>
                                            </c:when>
                                            <c:when test="${ticket.status eq 'in_progress'}">
                                                <span class="pill progress">
                                                    <i class="fas fa-clock"></i> In Progress
                                                </span>
                                            </c:when>
                                            <c:when test="${ticket.status eq 'resolved'}">
                                                <span class="pill done">
                                                    <i class="fas fa-check-circle"></i> Resolved
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pill" style="background:rgba(156,163,175,0.15);color:#d1d5db;">
                                                    <i class="fas fa-ban"></i> ${ticket.status}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${ticket.createdDate}" pattern="MMM dd, yyyy" var="createdDateStr"/>
                                        <fmt:formatDate value="${ticket.createdDate}" pattern="HH:mm" var="createdTimeStr"/>
                                        <div>${createdDateStr}</div>
                                        <small class="muted">${createdTimeStr}</small>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${ticket.lastUpdated}" pattern="MMM dd, yyyy" var="updatedDateStr"/>
                                        <fmt:formatDate value="${ticket.lastUpdated}" pattern="HH:mm" var="updatedTimeStr"/>
                                        <div>${updatedDateStr}</div>
                                        <small class="muted">${updatedTimeStr}</small>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=viewTicket&ticketId=${ticket.ticketId}"
                                           class="btn ghost"
                                           style="padding:6px 12px;font-size:12px;">
                                            <i class="fas fa-eye"></i> View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="text-align:center;padding:60px;color:var(--muted);">
                                    <i class="fas fa-ticket-alt" style="font-size:48px;margin-bottom:16px;display:block;opacity:0.3;"></i>
                                    <h3>No support tickets yet</h3>
                                    <p>When you create support tickets, they will appear here.</p>
                                    <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=showCreateForm" class="btn primary" style="margin-top:16px;">
                                        <i class="fas fa-plus"></i> Create Your First Ticket
                                    </a>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <!-- Quick Actions Section -->
    <section class="card" style="margin-top:16px;">
        <div class="body">
            <h2><i class="fas fa-rocket"></i> Need Help?</h2>
            <div class="grid two">
                <div>
                    <h3>Create Support Ticket</h3>
                    <p class="muted">Have an issue that needs our attention? Create a support ticket and we'll help you resolve it.</p>
                    <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=showCreateForm" class="btn primary">
                        <i class="fas fa-ticket-alt"></i> Create Ticket
                    </a>
                </div>
                <div>
                    <h3>General Feedback</h3>
                    <p class="muted">Share your thoughts about RhythmWave and help us improve our platform.</p>
                    <a href="${pageContext.request.contextPath}/FeedbackServlet?action=showFeedbackForm" class="btn ghost">
                        <i class="fas fa-comment"></i> Give Feedback
                    </a>
                </div>
            </div>
        </div>
    </section>

</main>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp" />

<script>
    // Filter tickets by status
    function filterTickets(status) {
        const rows = document.querySelectorAll('.ticket-row');
        const filterButtons = document.querySelectorAll('[id^="filter-"]');

        // Reset all button styles
        filterButtons.forEach(btn => {
            btn.classList.remove('active');
            btn.style.background = 'transparent';
            btn.style.color = 'var(--text)';
        });

        // Highlight active filter
        const activeBtn = document.getElementById('filter-' + status);
        if (activeBtn) {
            activeBtn.style.background = 'var(--accent)';
            activeBtn.style.color = 'white';
        }

        // Show/hide rows based on filter
        rows.forEach(row => {
            if (status === 'all') {
                row.style.display = '';
            } else {
                const rowStatus = row.getAttribute('data-status');
                row.style.display = rowStatus === status ? '' : 'none';
            }
        });

        // Update visible count
        updateTicketCount(status);
    }

    function updateTicketCount(filter) {
        const allRows = document.querySelectorAll('.ticket-row');
        const visibleRows = document.querySelectorAll('.ticket-row[style=""], .ticket-row:not([style])');

        // You could add a count display here if needed
        console.log(`Showing ${visibleRows.length} of ${allRows.length} tickets`);
    }

    // Initialize with 'all' filter active
    document.addEventListener('DOMContentLoaded', function() {
        filterTickets('all');

        // Auto-refresh ticket status every 30 seconds
        setInterval(function() {
            // You could implement AJAX refresh here
            // refreshTicketStatuses();
        }, 30000);
    });

    // Add some interactivity to table rows
    document.querySelectorAll('.ticket-row').forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#0b1222';
        });

        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
</script>

</body>
</html>