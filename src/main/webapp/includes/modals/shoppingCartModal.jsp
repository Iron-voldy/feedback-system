<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="modal fade cart-modal" id="shoppingCartModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Your Shopping Cart <span class="cart-badge"> <c:out value="${fn:length(sessionScope.cartItems)}" /></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <!-- Cart Item 1 -->
                <c:choose>
                    <c:when test="${empty sessionScope.cartItems}">
                        <div class="modal-body">
                            <div class="cart-empty">
                                <i class="fas fa-shopping-cart"></i>
                                <p>Your cart is empty</p>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="track" items="${sessionScope.cartItems}">
                            <div class="cart-item">
                                <img src="https://images.unsplash.com/photo-1571330735066-03aaa9429d89?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=800&q=80"
                                     alt="Album Cover" class="cart-item-img">
                                <div class="cart-item-details">
                                    <div class="cart-item-title">${track.title}</div>
                                    <div class="cart-item-artist">by ${track.artist}</div>
                                    <div class="cart-item-price">Rs. ${track.price}</div>
                                </div>
                                <a href="${pageContext.request.contextPath}/CartServlet?action=remove&trackId=${track.trackId}">
                                    <button class="cart-item-remove">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </a>

                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

            </div>
            <div class="modal-footer">
                <div class="cart-summary">
                    <span>Total: </span>
                    <span class="cart-total">Rs. ${sessionScope.cartTotal}</span>
                </div>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Continue Shopping</button>
                <button type="button" class="btn btn-primary">Proceed to Checkout</button>
            </div>
        </div>
    </div>
</div>
