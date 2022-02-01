<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
<body>
<strong>Please provide us with the following</strong>
<form action="register" method="post">
    <input type="text" id="name" name="name" placeholder="Name" required autofocus>
    <input type="email" id="email" name="email" placeholder="Email" required>
    <c:if test="${errorMsg}">
        <div class="invalid-register-message">
            <h4 align="center">Email is already used</h4>
        </div>
    </c:if>
    <input type="password" id="password" name="password" placeholder="Password" required>
    <input type="text" id="address" name="address"  placeholder="Address"/>
    <input type="submit" class="register-button" value="Register">

</form>
<button onclick="window.location.href='index.jsp'">GO BACK</button>
</body>
</html>