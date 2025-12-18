<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - Online Pharmacy</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 400px; margin: 0 auto; border: 1px solid #ddd; padding: 30px; border-radius: 5px; }
        .error { color: red; margin-bottom: 15px; }
        input[type="text"], input[type="password"], input[type="email"] {
            width: 100%; padding: 8px; margin: 5px 0 15px;
        }
        button { width: 100%; padding: 10px; background: #28a745; color: white; border: none; border-radius: 5px; }
        .links { margin-top: 20px; text-align: center; }
    </style>
</head>
<body>

<div class="container">
    <h2>Create Account</h2>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <c:if test="${not empty validationErrors}">
        <div class="error">
            <ul>
                <c:forEach var="error" items="${validationErrors}">
                    <li>${error}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/auth" method="post">
        <input type="hidden" name="action" value="register">

        <label>Username:</label>
        <input type="text" name="username" value="${param.username}" required>

        <label>Email:</label>
        <input type="email" name="email" value="${param.email}" required>

        <label>Password:</label>
        <input type="password" name="password" required>

        <label>Confirm Password:</label>
        <input type="password" name="confirmPassword" required>

        <button type="submit">Register</button>
    </form>

    <div class="links">
        <p>Already have an account?
            <a href="${pageContext.request.contextPath}/auth?action=login">Login here</a>
        </p>
        <p>Return to
            <a href="${pageContext.request.contextPath}/">Home page</a>
        </p>
    </div>
</div>

<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        var password = document.querySelector('input[name="password"]').value;
        var confirmPassword = document.querySelector('input[name="confirmPassword"]').value;

        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            e.preventDefault();
            return false;
        }

        if (password.length < 6) {
            alert('Password must be at least 6 characters long');
            e.preventDefault();
            return false;
        }
    });
</script>

</body>
</html>