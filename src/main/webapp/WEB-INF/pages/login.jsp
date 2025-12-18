<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Online Pharmacy</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 400px; margin: 0 auto; border: 1px solid #ddd; padding: 30px; border-radius: 5px; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
        input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin: 5px 0 15px; }
        button { width: 100%; padding: 10px; background: #007bff; color: white; border: none; border-radius: 5px; }
        .links { margin-top: 20px; text-align: center; }
    </style>
</head>
<body>

<div class="container">
    <h2>Login to Online Pharmacy</h2>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="success">${success}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/auth" method="post">
        <input type="hidden" name="action" value="login">

        <label>Username:</label>
        <input type="text" name="username" required>

        <label>Password:</label>
        <input type="password" name="password" required>

        <button type="submit">Login</button>
    </form>

    <div class="links">
        <p>Don't have an account?
            <a href="${pageContext.request.contextPath}/auth?action=register">Register here</a>
        </p>
        <p>Return to
            <a href="${pageContext.request.contextPath}/">Home page</a>
        </p>
    </div>
</div>

</body>
</html>