<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Welcome back, ${client.getName()}</title>
</head>

<form method="post" action="clientProfile">
<body>
    <div>
        <h4>Member since: ${client.getCreationDate()}</h4>
        <h4>Your current balance: ${client.getWallet().getBalance()}</h4>
        <input type="text" placeholder="Value $" name="chrgValue"/>
        <button name="btn" value="vl" >CHARGE</button>
        <h4>Email: ${client.getEmail()}</h4>
        <input type="text" placeholder="New Email" name="newEmail"/>
        <button name="btn" value="ml"  >CHANGE</button>
        <h4>Name: ${client.getName()}</h4>
        <input type="text" placeholder="New Name" name="newName"/>
        <button name="btn" value="nm" >CHANGE</button>
        <h4>Password:</h4>
        <input type="password" placeholder="New Password" name="newPassword"/>
        <button name="btn" value="pw" >CHANGE</button>
        <h4>Your address: ${client.getAddress()}</h4>
        <input type="text" placeholder="New Address" name="newAddress"/>
        <button name="btn" value="addrss" >CHANGE</button>
        <h4>Delete Account</h4>
        <button name="btn" value="dl" >DELETE</button>
    </div>
    <br>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
    <a href="${pageContext.request.contextPath}/secured/home">GO BACK</a>

</body>
</form>
</html>
