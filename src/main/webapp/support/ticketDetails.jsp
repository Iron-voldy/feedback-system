<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Ticket Details - ${requestScope.ticket.subject}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feedback-support.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<header>
    <div class="wrap" style="display:flex;align-items:center;justify-content:space-between;gap:16px">
        <div>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/">Home</a>
                <span>></span>
                <c:choose>
                    <c:when test="${sessionScope.USER.admin}">
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminAllTickets">Support Tickets</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=userTickets">My Tickets</a>
                    </c:otherwise>
                </c:choose>
                <span>></span>
                <span>Ticket #${requestScope.ticket.ticketId}</span>
            </div>
            <h1>Support Ticket Details</h1>
        </div>
        <div class="stack">
            <c:choose>
                <c:when test="${sessionScope.USER.admin}">
                    <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=adminAllTickets" class="btn ghost">
                        <i class="fas fa-arrow-left"></i> Back to All Tickets
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=userTickets" class="btn ghost">
                        <i class="fas fa-arrow-left"></i> Back to My Tickets
                    </a>
                </c:otherwise>
            </c:choose>
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

    <!-- Ticket Details -->
    <div class="grid two">
        <!-- Left Column - Ticket Information -->
        <section class="card">
            <div class="body">
                <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:16px;">
                    <div>
                        <h2><i class="fas fa-ticket-alt"></i> Ticket #${requestScope.ticket.ticketId}</h2>
                        <p class="muted">Created by ${requestScope.ticket.userName}</p>
                    </div>
                    <div style="text-align:right;">
                        <div style="margin-bottom:8px;">
                            <c:choose>
                                <c:when test="${requestScope.ticket.status eq 'open'}">
                                    <span class="pill open"><i class="fas fa-circle"></i> ${requestScope.ticket.status}</span>
                                </c:when>
                                <c:when test="${requestScope.ticket.status eq 'in_progress'}">
                                    <span class="pill progress"><i class="fas fa-clock"></i> In Progress</span>
                                </c:when>
                                <c:when test="${requestScope.ticket.status eq 'resolved'}">
                                    <span class="pill done"><i class="fas fa-check-circle"></i> Resolved</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="pill" style="background:rgba(156,163,175,0.15);color:#d1d5db;">${requestScope.ticket.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div>
                            <c:choose>
                                <c:when test="${requestScope.ticket.priority eq 'urgent'}">
                                    <span class="pill danger"><i class="fas fa-exclamation-triangle"></i> Urgent</span>
                                </c:when>
                                <c:when test="${requestScope.ticket.priority eq 'high'}">
                                    <span class="pill" style="background:rgba(239,68,68,0.15);color:#fecaca;"><i class="fas fa-arrow-up"></i> High</span>
                                </c:when>
                                <c:when test="${requestScope.ticket.priority eq 'medium'}">
                                    <span class="pill" style="background:rgba(245,158,11,0.16);color:#ffdd9a;"><i class="fas fa-minus"></i> Medium</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="pill" style="background:rgba(34,197,94,0.18);color:#bbf7d0;"><i class="fas fa-arrow-down"></i> Low</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="row" style="margin-bottom:16px;">
                    <div>
                        <label>Type</label>
                        <div style="text-transform:capitalize;">${requestScope.ticket.ticketType}</div>
                    </div>
                    <div>
                        <label>Created</label>
                        <fmt:formatDate value="${requestScope.ticket.createdDate}" pattern="MMM dd, yyyy HH:mm" var="createdStr"/>
                        <div>${createdStr}</div>
                    </div>
                </div>

                <div style="margin-bottom:16px;">
                    <label>Subject</label>
                    <div style="font-weight:600;">${requestScope.ticket.subject}</div>
                </div>

                <div>
                    <label>Description</label>
                    <div style="background:#0b1020;padding:12px;border-radius:8px;border:1px solid var(--border);">
                        ${fn:escapeXml(requestScope.ticket.description)}
                    </div>
                </div>

                <c:if test="${not empty requestScope.ticket.assignedAdminName}">
                    <div style="margin-top:16px;padding-top:16px;border-top:1px solid var(--border);">
                        <label>Assigned to</label>
                        <div><i class="fas fa-user-tie"></i> ${requestScope.ticket.assignedAdminName}</div>
                    </div>
                </c:if>
            </div>
        </section>

        <!-- Right Column - Admin Actions (if admin) -->
        <c:if test="${sessionScope.USER.admin}">
            <section class="card">
                <div class="body">
                    <h2><i class="fas fa-cogs"></i> Admin Actions</h2>

                    <!-- Status Update -->
                    <form action="${pageContext.request.contextPath}/SupportTicketServlet" method="post" style="margin-bottom:16px;">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="ticketId" value="${requestScope.ticket.ticketId}">
                        <label for="status">Update Status</label>
                        <div class="row">
                            <select id="status" name="status" required>
                                <option value="open" ${requestScope.ticket.status eq 'open' ? 'selected' : ''}>Open</option>
                                <option value="in_progress" ${requestScope.ticket.status eq 'in_progress' ? 'selected' : ''}>In Progress</option>
                                <option value="resolved" ${requestScope.ticket.status eq 'resolved' ? 'selected' : ''}>Resolved</option>
                                <option value="closed" ${requestScope.ticket.status eq 'closed' ? 'selected' : ''}>Closed</option>
                            </select>
                            <button type="submit" class="btn primary">Update</button>
                        </div>
                    </form>

                    <!-- Priority Update -->
                    <form action="${pageContext.request.contextPath}/SupportTicketServlet" method="post" style="margin-bottom:16px;">
                        <input type="hidden" name="action" value="updatePriority">
                        <input type="hidden" name="ticketId" value="${requestScope.ticket.ticketId}">
                        <label for="priority">Update Priority</label>
                        <div class="row">
                            <select id="priority" name="priority" required>
                                <option value="low" ${requestScope.ticket.priority eq 'low' ? 'selected' : ''}>Low</option>
                                <option value="medium" ${requestScope.ticket.priority eq 'medium' ? 'selected' : ''}>Medium</option>
                                <option value="high" ${requestScope.ticket.priority eq 'high' ? 'selected' : ''}>High</option>
                                <option value="urgent" ${requestScope.ticket.priority eq 'urgent' ? 'selected' : ''}>Urgent</option>
                            </select>
                            <button type="submit" class="btn danger">Update</button>
                        </div>
                    </form>

                    <!-- Assign Ticket -->
                    <form action="${pageContext.request.contextPath}/SupportTicketServlet" method="post">
                        <input type="hidden" name="action" value="assignTicket">
                        <input type="hidden" name="ticketId" value="${requestScope.ticket.ticketId}">
                        <label for="adminId">Assign to Admin</label>
                        <div class="row">
                            <select id="adminId" name="adminId" required>
                                <option value="">Select admin...</option>
                                <option value="${sessionScope.USER.userId}">${sessionScope.USER.firstName} ${sessionScope.USER.lastName} (Me)</option>
                                <!-- Add other admin users here -->
                            </select>
                            <button type="submit" class="btn success">Assign</button>
                        </div>
                    </form>
                </div>
            </section>
        </c:if>
    </div>

    <!-- Response Thread -->
    <section class="card" style="margin-top:16px;">
        <div class="body">
            <h2><i class="fas fa-comments"></i> Conversation</h2>

            <!-- Responses -->
            <div style="margin:16px 0;">
                <c:choose>
                    <c:when test="${not empty requestScope.ticket.responses}">
                        <c:forEach var="response" items="${requestScope.ticket.responses}">
                            <div style="background:${response.adminResponse ? '#0b1222' : '#0f172a'};border:1px solid var(--border);border-radius:12px;padding:16px;margin-bottom:12px;${response.adminResponse ? 'border-left:4px solid var(--accent);' : 'border-left:4px solid var(--accent-2);'}">
                                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                                    <div style="display:flex;align-items:center;gap:8px;">
                                        <c:choose>
                                            <c:when test="${response.adminResponse}">
                                                <i class="fas fa-user-tie" style="color:var(--accent);"></i>
                                                <strong>${response.responderName}</strong>
                                                <span class="pill" style="background:rgba(99,102,241,0.15);color:#c7c9ff;font-size:10px;">ADMIN</span>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-user" style="color:var(--accent-2);"></i>
                                                <strong>${response.responderName}</strong>
                                                <span class="pill" style="background:rgba(34,197,94,0.18);color:#bbf7d0;font-size:10px;">CUSTOMER</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <small class="muted">
                                        <fmt:formatDate value="${response.responseDate}" pattern="MMM dd, yyyy HH:mm" />
                                    </small>
                                </div>
                                <div>${fn:escapeXml(response.responseText)}</div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="text-align:center;padding:40px;color:var(--muted);">
                            <i class="fas fa-comment" style="font-size:48px;margin-bottom:16px;display:block;opacity:0.3;"></i>
                            No responses yet
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Add Response Form -->
            <c:if test="${requestScope.ticket.status ne 'closed'}">
                <div style="border-top:1px solid var(--border);padding-top:16px;">
                    <form action="${pageContext.request.contextPath}/SupportTicketServlet" method="post">
                        <input type="hidden" name="action" value="addResponse">
                        <input type="hidden" name="ticketId" value="${requestScope.ticket.ticketId}">

                        <label for="responseText">Add Response</label>
                        <textarea id="responseText" name="responseText" placeholder="Type your response here..." required></textarea>
                        <div style="height:12px"></div>

                        <button type="submit" class="btn primary">
                            <i class="fas fa-reply"></i> Send Response
                        </button>
                    </form>
                </div>
            </c:if>

            <c:if test="${requestScope.ticket.status eq 'closed'}">
                <div style="text-align:center;padding:20px;color:var(--muted);border-top:1px solid var(--border);margin-top:16px;">
                    <i class="fas fa-lock"></i> This ticket is closed. No further responses can be added.
                </div>
            </c:if>
        </div>
    </section>

</main>

<script>
    // Auto-focus on response textarea when page loads
    window.addEventListener('load', function() {
        const responseTextArea = document.getElementById('responseText');
        if (responseTextArea && !responseTextArea.hasAttribute('readonly')) {
            responseTextArea.focus();
        }
    });

    // Form validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.classList.add('loading');
                submitBtn.disabled = true;
            }
        });
    });
</script>

</body>
</html>