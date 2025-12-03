<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login - Online Pharmacy</title>
</head>
<body>
<h1>Login</h1>

<c:if test="${not empty error}">
    <p style="color: red;">${error}</p>
</c:if>

<c:if test="${not empty success}">
    <p style="color: green;">${success}</p>
</c:if>

<form action="${pageContext.request.contextPath}/auth" method="post">
    <input type="hidden" name="action" value="login">

    <label>Username:</label>
    <input type="text" name="username" required><br>

    <label>Password:</label>
    <input type="password" name="password" required><br>

    <button type="submit">Login</button>
</form>

<p>Don't have an account? <a href="${pageContext.request.contextPath}/auth?action=register">Register here</a></p>
</body>
</html>