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
<strong>Please insert your credentials</strong>
<form action="loginCm" method="post">
  <input type="email" id="email" name="email" placeholder="Email" maxlength="150" required autofocus>
  <input type="password" id="password" name="password" placeholder="Password" maxlength="15" required>
  <c:if test="${errorMsg}">
    <div class="invalid-login-message">
      <h4 align="center">Invalid Credentials</h4>
    </div>
  </c:if>
  <input type="submit" value="Login">
</form>
<button onclick="window.location.href='index.jsp'">GO BACK</button>
</body>
</html>