<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${empty requestScope.noOfPages}">
    <c:redirect url="/trackPaginate"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RhythmWave Music Store</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <style>

    </style>

</head>
<body>

<!-- navigation bar -->
<jsp:include page="/includes/navbar.jsp">
    <jsp:param name="page" value="index"/>
</jsp:include>

<!-- Banner Section -->
<div class="container">
    <div class="banner">
        <c:choose>
            <c:when test="${empty sessionScope.USER}">
                <div class="container text-center">
                    <h1 class="display-4 fw-bold">Discover Your Soundtrack</h1>
                    <p class="lead">Millions of songs at your fingertips. Curated playlists and exclusive releases.</p>
                    <button class="btn btn-primary btn-lg mt-3">Explore Now</button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="container text-center">
                    <h1 class="display-4 fw-bold">Welcome Back! ${sessionScope.USER.firstName}</h1>
                    <p class="lead">Millions of songs at your fingertips. Curated playlists and exclusive releases.</p>
                    <button class="btn btn-primary btn-lg mt-3">Explore Now</button>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</div>

<!-- Music Listing Section -->
<div class="container">
    <h2 class="mb-4">Featured Music</h2>
    <div class="row">

        <!-- Music Card -->
        <c:forEach var="track" items="${requestScope.trackList}">
            <div class="col-md-4 col-lg-3 mb-3">
                <div class="music-card">
                    <img src="https://images.unsplash.com/photo-1571330735066-03aaa9429d89?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=800&q=80" class="album-cover" alt="Album Cover">
                    <div class="p-3">
                        <h5>${track.title}</h5>
                        <p class="artist-name">by ${track.artist}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price-tag">Rs. ${track.price}</span>
                            <div class="d-flex">
                                <div class="play-btn me-2">
                                    <i class="fas fa-play"></i>
                                </div>

                                <a href="${pageContext.request.contextPath}/CartServlet?action=add&trackId=${track.trackId}">
                                    <div class="cart-btn">
                                        <i class="fas fa-cart-plus"></i>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <c:set var="currentPage" value="${requestScope.currentPage}"/>
        <c:set var="noOfPages" value="${requestScope.noOfPages}"/>

        <!-- Pagination -->
        <c:if test="${noOfPages > 1}">
            <nav class="mt-5">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/trackPaginate?page=${currentPage - 1}">Previous</a>
                    </li>
                    <c:forEach var="i" begin="1" end="${noOfPages}">
                        <c:choose>
                            <c:when test="${i eq currentPage}">
                                <li class="page-item active"><a class="page-link" href="${pageContext.request.contextPath}/trackPaginate?page=${i}">${i}</a></li>
                            </c:when>
                            <c:otherwise>
                                <li class="page-item"><a class="page-link" href="${pageContext.request.contextPath}/trackPaginate?page=${i}">${i}</a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <li class="page-item ${currentPage == noOfPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/trackPaginate?page=${currentPage + 1}">Next</a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>

</div>

<!-- Footer -->
<jsp:include page="includes/footer.jsp" />

<!-- Shopping Cart Modal -->
<jsp:include page="includes/modals/shoppingCartModal.jsp" />

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Simple interactivity for the music cards
    document.addEventListener('DOMContentLoaded', function() {
        // Play button functionality
        const playButtons = document.querySelectorAll('.play-btn');
        playButtons.forEach(button => {
            button.addEventListener('click', function() {
                this.classList.toggle('playing');
                const icon = this.querySelector('i');
                if (icon.classList.contains('fa-play')) {
                    icon.classList.replace('fa-play', 'fa-pause');
                } else {
                    icon.classList.replace('fa-pause', 'fa-play');
                }
            });
        });

        // Add to cart functionality
        const cartButtons = document.querySelectorAll('.cart-btn');
        cartButtons.forEach(button => {
            button.addEventListener('click', function() {
                this.classList.toggle('added');
                const icon = this.querySelector('i');
                if (this.classList.contains('added')) {
                    icon.classList.replace('fa-cart-plus', 'fa-check');
                } else {
                    icon.classList.replace('fa-check', 'fa-cart-plus');
                }
            });
        });
    });
</script>
</body>
</html>