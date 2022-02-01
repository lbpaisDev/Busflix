<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Welcome back, ${cm.getName()}</title>
</head>
<body>

<form action="homeCm" method="get">
    <div>
        <br>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
        <br>
        <a href="${pageContext.request.contextPath}/secured/manageTrips">Manage Trips</a>
        <br>
    </div>
    <div>
            <H2>TOP 5 Clients</H2>
        <table border="1" width="auto" style="float: left">
        <th>Client Name</th>
            <th>Client Email</th>
            <th>Client Address</th>
            <th>Number of trips</th>
            <c:forEach var="clnt" items="${clnts}">
                <tr>
                    <td align="center">
                        <p>${clnt.getName()}</p>
                    </td>
                    <td align="center">
                        <p>${clnt.getEmail()}</p>
                    </td>
                    <td align="center">
                        <p>${clnt.getAddress()}</p>
                    </td>
                    <td align="center">
                        <p>${clnt.getNtrips()}</p>
                    </td>
                </tr>
            </c:forEach>
            </tr>
        </table>
    </div>
</form>
<form action = "homeCm" method="post">
    <div>
        <table border="1" width="auto" style="float: left">
        <th>Get Daily Revenue</th>
        <th>Revenue</th>
            <tr>
                <td align="center">
                    <button name="rev" value = "val" type="submit">GET REVENUE</button>
                </td>
                <td align="center">
                    <p>${rev}</p>
                </td>
            </tr>
        </table>

    </div>
</form>
</body>
</html>