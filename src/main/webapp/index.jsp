<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Online Pharmacy - Home</title>
</head>
<body>
<h1>Welcome to Online Pharmacy!</h1>

<div style="margin: 50px auto; width: 300px; border: 1px solid #ccc; padding: 20px; border-radius: 10px;">

    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            <p>Welcome back, ${sessionScope.user.firstName}!</p>
            <p>You are logged in as: ${sessionScope.user.role}</p>
            <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
        </c:when>
        <c:otherwise>
            <h3>Login to your account</h3>

            <c:if test="${not empty requestScope.error}">
                <p style="color: red;">${requestScope.error}</p>
            </c:if>

            <c:if test="${not empty requestScope.success}">
                <p style="color: green;">${requestScope.success}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}/auth" method="post">
                <input type="hidden" name="action" value="login">

                <label>Username:</label><br>
                <input type="text" name="username" required style="width: 100%; padding: 5px; margin-bottom: 10px;"><br>

                <label>Password:</label><br>
                <input type="password" name="password" required style="width: 100%; padding: 5px; margin-bottom: 15px;"><br>

                <button type="submit" style="width: 100%; padding: 10px; background: #007bff; color: white; border: none; border-radius: 5px;">
                    Login
                </button>
            </form>

            <hr style="margin: 20px 0;">

            <p>Don't have an account?
                <a href="${pageContext.request.contextPath}/auth?action=register">Register here</a>
            </p>

            <p>Or continue as guest:
                <a href="${pageContext.request.contextPath}/client/catalog">Browse medicines</a>
            </p>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>