<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register - Online Pharmacy</title>
</head>
<body>
<h1>Register</h1>

<c:if test="${not empty error}">
    <p style="color: red;">${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/auth" method="post">
    <input type="hidden" name="action" value="register">

    <label>Username:</label>
    <input type="text" name="username" required><br>

    <label>Email:</label>
    <input type="email" name="email" required><br>

    <label>First Name:</label>
    <input type="text" name="firstName" required><br>

    <label>Last Name:</label>
    <input type="text" name="lastName" required><br>

    <label>Password:</label>
    <input type="password" name="password" required><br>

    <button type="submit">Register</button>
</form>

<p>Already have an account? <a href="${pageContext.request.contextPath}/auth?action=login">Login here</a></p>
</body>
</html>