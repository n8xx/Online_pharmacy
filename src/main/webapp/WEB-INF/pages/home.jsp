<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home - Online Pharmacy</title>
</head>
<body>
<h1>Welcome to Online Pharmacy</h1>

<p>Hello, ${user.firstName} ${user.lastName} (${user.role})</p>

<h2>Navigation</h2>
<ul>
    <c:if test="${user.role == 'CLIENT'}">
        <li><a href="${pageContext.request.contextPath}/client/catalog">Browse Medicines</a></li>
        <li><a href="${pageContext.request.contextPath}/client/cart">My Cart</a></li>
        <li><a href="${pageContext.request.contextPath}/client/orders">My Orders</a></li>
        <li><a href="${pageContext.request.contextPath}/client/prescriptions">My Prescriptions</a></li>
    </c:if>

    <c:if test="${user.role == 'DOCTOR'}">
        <li><a href="${pageContext.request.contextPath}/doctor/requests">Prescription Requests</a></li>
        <li><a href="${pageContext.request.contextPath}/doctor/patients">My Patients</a></li>
    </c:if>

    <c:if test="${user.role == 'PHARMACIST'}">
        <li><a href="${pageContext.request.contextPath}/pharmacist/medicines">Manage Medicines</a></li>
        <li><a href="${pageContext.request.contextPath}/pharmacist/orders">Process Orders</a></li>
    </c:if>

    <c:if test="${user.role == 'ADMIN'}">
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/reports">View Reports</a></li>
    </c:if>

    <li><a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a></li>
</ul>
</body>
</html>