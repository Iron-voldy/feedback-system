<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Music - RhythmWave Music Store</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <style>
        /* Ensure consistency with theme.css and index.css */
        body {
            background-color: var(--dark-bg);
            color: var(--text-primary);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .search-header {
            background: linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.7)),
            url('https://images.unsplash.com/photo-1511379938547-c1f69419868d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1770&q=80');
            background-size: cover;
            background-position: center;
            padding: 2rem;
            border-radius: 10px;
            margin-bottom: 2rem;
        }

        .search-header h1 {
            color: var(--text-primary);
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
        }

        .input-group input {
            background-color: #2a2a2a;
            border: 1px solid #444;
            color: var(--text-primary);
        }

        .input-group input::placeholder {
            color: var(--placeholder-color);
        }

        .input-group input:focus {
            background-color: #2a2a2a;
            border-color: var(--primary);
            box-shadow: 0 0 0 0.25rem rgba(187, 134, 252, 0.25);
        }

        .search-section {
            background-color: var(--card-bg);
            padding: 1.5rem;
            border-radius: 10px;
            margin-bottom: 2rem;
        }

        .filter-label {
            color: var(--text-secondary);
            font-size: 0.9rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .form-select {
            background-color: #2a2a2a;
            border: 1px solid #444;
            color: var(--text-primary);
            border-radius: 5px;
        }

        .form-select:focus {
            background-color: #2a2a2a;
            border-color: var(--primary);
            box-shadow: 0 0 0 0.25rem rgba(187, 134, 252, 0.25);
        }

        .btn-primary {
            background-color: var(--primary);
            border-color: var(--primary);
            color: #000;
            transition: all 0.3s ease;
        }

        .btn-primary:hover {
            background-color: #9965f4;
            border-color: #9965f4;
            transform: translateY(-2px);
        }

        .btn-outline-secondary {
            border-color: var(--primary);
            color: var(--primary);
            transition: all 0.3s ease;
        }

        .btn-outline-secondary:hover {
            background-color: var(--primary);
            color: #000;
            transform: translateY(-2px);
        }

        .search-results-info {
            color: var(--text-secondary);
            margin-bottom: 1.5rem;
        }

        .no-results {
            text-align: center;
            padding: 3rem;
            color: var(--text-secondary);
            background-color: var(--card-bg);
            border-radius: 10px;
        }

        .no-results i {
            font-size: 3rem;
            margin-bottom: 1rem;
            display: block;
            color: #555;
        }

        .music-card {
            background-color: var(--card-bg);
            border-radius: 8px;
            transition: transform 0.3s, box-shadow 0.3s;
            overflow: hidden;
        }

        .music-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.3);
        }

        .album-cover {
            width: 100%;
            aspect-ratio: 1/1;
            object-fit: cover;
            border-top-left-radius: 8px;
            border-top-right-radius: 8px;
        }

        .play-btn, .cart-btn {
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            transition: all 0.3s ease;
        }

        .play-btn {
            background-color: var(--primary);
            color: #000;
        }

        .play-btn:hover {
            background-color: #9965f4;
            transform: scale(1.1);
        }

        .play-btn.playing {
            background-color: var(--secondary);
            animation: pulse 0.5s infinite;
        }

        .cart-btn {
            background-color: transparent;
            border: 1px solid var(--primary);
            color: var(--primary);
        }

        .cart-btn:hover {
            background-color: var(--primary);
            color: #000;
            transform: scale(1.1);
        }

        .cart-btn.added {
            background-color: var(--primary);
            color: #000;
        }

        .price-tag {
            color: var(--primary);
            font-weight: bold;
        }

        .artist-name {
            color: var(--secondary);
        }

        /* Animation for play button */
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
    </style>
</head>
<body>
<!-- Navigation Bar -->
<jsp:include page="/includes/navbar.jsp">
    <jsp:param name="page" value="search"/>
    <jsp:param name="searchBox" value="noShow"/>
</jsp:include>

<!-- Search Header Section -->
<div class="container mt-4">
    <div class="search-header">
        <h1 class="display-5 fw-bold mb-4">Search Our Music Library</h1>
        <!-- Main Search Bar -->
        <div class="row justify-content-center">
            <div class="col-md-8">
                <form action="${pageContext.request.contextPath}/search" method="get">
                    <div class="input-group input-group-lg mb-4">
                        <input type="text" class="form-control" name="query" placeholder="Search artists, albums, or songs..." value="${param.query}">
                        <button class="btn btn-primary" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Advanced Filters Section -->
<div class="container">
    <div class="search-section">
        <h2 class="mb-4">Refine Your Search</h2>
        <form action="${pageContext.request.contextPath}/search" method="get">
            <input type="hidden" name="query" value="${param.query}">
            <div class="row mt-3">
                <div class="col-md-3">
                    <div class="filter-label">Genre</div>
                    <select class="form-select" name="genre">
                        <option value="">All Genres</option>
                        <option value="rock" ${param.genre == 'rock' ? 'selected' : ''}>Rock</option>
                        <option value="pop" ${param.genre == 'pop' ? 'selected' : ''}>Pop</option>
                        <option value="hiphop" ${param.genre == 'hiphop' ? 'selected' : ''}>Hip Hop</option>
                        <option value="electronic" ${param.genre == 'electronic' ? 'selected' : ''}>Electronic</option>
                        <option value="jazz" ${param.genre == 'jazz' ? 'selected' : ''}>Jazz</option>
                        <option value="classical" ${param.genre == 'classical' ? 'selected' : ''}>Classical</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <div class="filter-label">Price Range</div>
                    <select class="form-select" name="price">
                        <option value="">Any Price</option>
                        <option value="under100" ${param.price == 'under100' ? 'selected' : ''}>Under Rs. 100</option>
                        <option value="100-200" ${param.price == '100-200' ? 'selected' : ''}>Rs. 100 - Rs. 200</option>
                        <option value="200-400" ${param.price == '200-400' ? 'selected' : ''}>Rs. 200 - Rs. 400</option>
                        <option value="over400" ${param.price == 'over400' ? 'selected' : ''}>Over Rs. 400</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <div class="filter-label">Release Year</div>
                    <select class="form-select" name="year">
                        <option value="">Any Year</option>
                        <option value="2023" ${param.year == '2023' ? 'selected' : ''}>2023</option>
                        <option value="2022" ${param.year == '2022' ? 'selected' : ''}>2022</option>
                        <option value="2020-2021" ${param.year == '2020-2021' ? 'selected' : ''}>2020-2021</option>
                        <option value="2010-2019" ${param.year == '2010-2019' ? 'selected' : ''}>2010-2019</option>
                        <option value="before2010" ${param.year == 'before2010' ? 'selected' : ''}>Before 2010</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <div class="filter-label">Rating</div>
                    <select class="form-select" name="rating">
                        <option value="">Any Rating</option>
                        <option value="5" ${param.rating == '5' ? 'selected' : ''}>5 Stars</option>
                        <option value="4" ${param.rating == '4' ? 'selected' : ''}>4+ Stars</option>
                        <option value="3" ${param.rating == '3' ? 'selected' : ''}>3+ Stars</option>
                    </select>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col-md-12 text-end">
                    <button type="submit" class="btn btn-primary">Apply Filters</button>
                    <a href="${pageContext.request.contextPath}/search?query=${param.query}" class="btn btn-outline-secondary ms-2">Clear Filters</a>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Search Results Section -->
<div class="container mt-5">
    <c:choose>
        <c:when test="${not empty requestScope.trackList}">
            <div class="search-results-info">
                <h4>${empty param.query ? 'All Tracks' : 'Search Results for "' += param.query += '"'}</h4>
                <p>Found ${fn:length(requestScope.trackList)} results</p>
            </div>
            <div class="row">
                <c:forEach var="track" items="${requestScope.trackList}">
                    <div class="col-md-4 col-lg-3 mb-4">
                        <div class="music-card">
                            <img src="https://images.unsplash.com/photo-1571330735066-03aaa9429d89?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=800&q=80" class="album-cover" alt="Album Cover">
                            <div class="p-3">
                                <h5>${track.title}</h5>
                                <p class="artist-name">by ${track.artist}</p>
                                <p class="genre">Genre: ${track.genre}</p>
                                <p class="rating">Rating: ${track.rating} Stars</p>
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
            </div>
            <!-- Pagination -->
            <c:if test="${requestScope.noOfPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center mt-4">
                        <c:if test="${requestScope.currentPage > 1}">
                            <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/search?query=${param.query}&genre=${param.genre}&price=${param.price}&rating=${param.rating}&page=${requestScope.currentPage - 1}">Previous</a>
                            </li>
                        </c:if>
                        <c:forEach begin="1" end="${requestScope.noOfPages}" var="i">
                            <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/search?query=${param.query}&genre=${param.genre}&price=${param.price}&rating=${param.rating}&page=${i}">${i}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${requestScope.currentPage < requestScope.noOfPages}">
                            <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/search?query=${param.query}&genre=${param.genre}&price=${param.price}&rating=${param.rating}&page=${requestScope.currentPage + 1}">Next</a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="no-results">
                <i class="fas fa-music"></i>
                <h4>No tracks available</h4>
                <p>No tracks found in the library. Please check back later.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Footer -->
<jsp:include page="/includes/footer.jsp" />

<!-- Shopping Cart Modal -->
<jsp:include page="/includes/modals/shoppingCartModal.jsp" />

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
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