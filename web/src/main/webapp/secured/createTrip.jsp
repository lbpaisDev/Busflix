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

<form method="post" action="createTrip">
    <body>
    <div>
        <input type="text" placeholder="Departure Time" name="dpt"/>
        <input type="text" placeholder="Arrival Time" name="arrt"/>
        <input type="text" placeholder="Origin" name="orgn"/>
        <input type="text" placeholder="Destination " name="dest"/>
        <input type="text" placeholder="Capacity" name="cpct"/>
        <input type="text" placeholder="Price" name="price"/>
        <button name="btn">CREATE</button>
    </div>
    <br>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
    <a href="${pageContext.request.contextPath}/secured/manageTrips">GO BACK</a>

    </body>
</form>
</html>
