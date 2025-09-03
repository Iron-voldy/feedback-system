<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Feedback & Support - RhythmWave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feedback-support.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<!-- Include navbar -->
<jsp:include page="/includes/navbar.jsp">
    <jsp:param name="page" value="feedback"/>
</jsp:include>

<header>
    <div class="wrap" style="display:flex;align-items:center;justify-content:space-between;gap:16px">
        <div>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/">Home</a>
                <span>></span>
                <span>Feedback & Support</span>
            </div>
            <h1>Feedback & Support</h1>
        </div>
        <div class="stack">
            <c:if test="${not empty sessionScope.USER}">
                <a href="${pageContext.request.contextPath}/FeedbackServlet?action=userFeedback" class="btn ghost">
                    <i class="fas fa-history"></i> My Feedback
                </a>
                <a href="${pageContext.request.contextPath}/SupportTicketServlet?action=userTickets" class="btn ghost">
                    <i class="fas fa-ticket-alt"></i> My Tickets
                </a>
            </c:if>
        </div>
    </div>
</header>

<main class="wrap grid two" style="margin-top:16px">

    <!-- Success/Error Messages -->
    <c:if test="${not empty param.success}">
        <div class="alert success" style="grid-column: 1/-1;">
            <i class="fas fa-check-circle"></i> Thank you for your feedback! We appreciate your input and will review it soon.
        </div>
    </c:if>

    <c:if test="${not empty requestScope.success}">
        <div class="alert success" style="grid-column: 1/-1;">
            <i class="fas fa-check-circle"></i> ${requestScope.success}
        </div>
    </c:if>

    <c:if test="${not empty requestScope.error}">
        <div class="alert error" style="grid-column: 1/-1;">
            <i class="fas fa-exclamation-circle"></i> ${requestScope.error}
        </div>
    </c:if>

    <!-- General Feedback Form -->
    <section class="card">
        <div class="body">
            <h2><i class="fas fa-comments"></i> General Feedback</h2>
            <p class="muted" style="margin-top:2px">Share your thoughts about our platform and help us improve.</p>

            <form action="${pageContext.request.contextPath}/FeedbackServlet" method="post" id="feedbackForm">
                <input type="hidden" name="action" value="submitFeedback">

                <!-- Feedback Type -->
                <label for="feedbackType">Type of Feedback</label>
                <select id="feedbackType" name="feedbackType" required>
                    <option value="">Select feedback type...</option>
                    <option value="general">General Feedback</option>
                    <option value="bug_report">Bug Report</option>
                    <option value="feature_request">Feature Request</option>
                </select>

                <div style="height:10px"></div>

                <!-- Subject -->
                <label for="subject">Subject</label>
                <input id="subject" name="subject" placeholder="Brief description of your feedback" required />

                <div style="height:10px"></div>

                <!-- Message -->
                <label for="message">Message</label>
                <textarea id="message" name="message" placeholder="Tell us more about your experience, suggestions, or issues..." required></textarea>

                <div style="height:10px"></div>

                <!-- Rating -->
                <label>Overall Rating (optional)</label>
                <div class="stars" id="stars"></div>
                <input type="hidden" id="ratingInput" name="rating" value="">

                <div style="height:10px"></div>

                <!-- Email for anonymous users -->
                <c:if test="${empty sessionScope.USER}">
                    <label for="email">Your Email</label>
                    <input id="email" name="email" type="email" placeholder="your.email@example.com" required />
                    <div style="height:10px"></div>
                </c:if>

                <button type="submit" class="btn primary">
                    <i class="fas fa-paper-plane"></i> Submit Feedback
                </button>
            </form>
        </div>
    </section>

    <!-- Support Ticket Form -->
    <section class="card">
        <div class="body">
            <h2><i class="fas fa-life-ring"></i> Submit a Support Ticket</h2>
            <p class="muted" style="margin-top:2px">Need help? Create a support ticket and our team will assist you.</p>

            <c:choose>
                <c:when test="${empty sessionScope.USER}">
                    <div class="alert error">
                        <i class="fas fa-info-circle"></i>
                        Please <a href="${pageContext.request.contextPath}/login.jsp" style="color: var(--accent);">login</a> to create a support ticket.
                    </div>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/SupportTicketServlet" method="post" id="ticketForm">
                        <input type="hidden" name="action" value="createTicket">

                        <!-- Ticket Type and Priority -->
                        <div class="row">
                            <div>
                                <label for="ticketType">Issue Type</label>
                                <select id="ticketType" name="ticketType" required>
                                    <option value="">Select issue type...</option>
                                    <option value="complaint">Complaint</option>
                                    <option value="refund">Refund Request</option>
                                    <option value="general">General Support</option>
                                </select>
                            </div>
                            <div>
                                <label for="priority">Priority</label>
                                <select id="priority" name="priority">
                                    <option value="low">Low</option>
                                    <option value="medium" selected>Medium</option>
                                    <option value="high">High</option>
                                    <option value="urgent">Urgent</option>
                                </select>
                            </div>
                        </div>

                        <div style="height:10px"></div>

                        <!-- Subject -->
                        <label for="ticketSubject">Subject</label>
                        <input id="ticketSubject" name="subject" placeholder="Brief description of your issue" required />

                        <div style="height:10px"></div>

                        <!-- Description -->
                        <label for="description">Describe the issue</label>
                        <textarea id="description" name="description" placeholder="Please provide detailed information about your issue. Include steps to reproduce if it's a bug, or specific details about what you need help with." required></textarea>

                        <div style="height:12px"></div>

                        <button type="submit" class="btn danger">
                            <i class="fas fa-ticket-alt"></i> Create Support Ticket
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

</main>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp" />

<script>
    // --- Stars component for feedback rating ---
    let currentRating = 0;
    const starsEl = document.getElementById('stars');
    const ratingInput = document.getElementById('ratingInput');

    if (starsEl) {
        const makeStar = i => {
            const el = document.createElementNS('http://www.w3.org/2000/svg','svg');
            el.setAttribute('viewBox','0 0 24 24');
            el.classList.add('star');
            el.innerHTML = '<path d="M12 .587l3.668 7.431 8.2 1.193-5.934 5.787 1.402 8.168L12 18.896 4.664 23.166l1.402-8.168L.132 9.211l8.2-1.193z"/>';
            el.addEventListener('mouseover',()=>paint(i));
            el.addEventListener('mouseleave',()=>paint(currentRating));
            el.addEventListener('click',()=>{currentRating=i; paint(i); ratingInput.value=i;});
            return el;
        }

        const paint = n => {
            [...starsEl.children].forEach((el,idx)=>{
                el.style.fill = (idx < n) ? '#fbbf24' : 'transparent';
                el.style.stroke = (idx < n) ? '#fbbf24' : '#475569';
                el.style.strokeWidth = '1.5px';
            })
        }

        for(let i=1;i<=5;i++) starsEl.appendChild(makeStar(i));
        paint(0);
    }

    // Form validation
    document.getElementById('feedbackForm').addEventListener('submit', function(e) {
        const feedbackType = document.getElementById('feedbackType').value;
        const subject = document.getElementById('subject').value.trim();
        const message = document.getElementById('message').value.trim();

        if (!feedbackType || !subject || !message) {
            e.preventDefault();
            alert('Please fill in all required fields.');
            return;
        }

        // Add loading state
        const submitBtn = e.target.querySelector('button[type="submit"]');
        submitBtn.classList.add('loading');
        submitBtn.disabled = true;
    });

    // Support ticket form validation (if exists)
    const ticketForm = document.getElementById('ticketForm');
    if (ticketForm) {
        ticketForm.addEventListener('submit', function(e) {
            const ticketType = document.getElementById('ticketType').value;
            const subject = document.getElementById('ticketSubject').value.trim();
            const description = document.getElementById('description').value.trim();

            if (!ticketType || !subject || !description) {
                e.preventDefault();
                alert('Please fill in all required fields.');
                return;
            }

            // Add loading state
            const submitBtn = e.target.querySelector('button[type="submit"]');
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;
        });
    }
</script>

</body>
</html>