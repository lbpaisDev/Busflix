<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Welcome back, ${client.getName()}</title>
</head>

<body>
<div>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
    <a href="${pageContext.request.contextPath}/secured/home">GO BACK</a>
    <div style = "display: inline">
    <form action="searchTrips" method="get">
        <input type="text" placeholder="Departure" name="departure"/>
        <input type="text" placeholder="Arrival" name="arrival"/>
        <button type="submit" name="button" value="button1">SEARCH</button>        <table border="1" width="auto" style = "float: left">
            <th>Trip Departure Time</th>
            <th>Trip Arrival Time</th>
            <th>Origin Location</th>
            <th>Destination Location</th>
            <th>Trip Price</th>
            <th>Number of seats left</th>
            <tr>
                <c:forEach var="trip" items="${trips}">
            <tr>
                <td>
                    <p>${trip.getDepartureTime()}</p>
                </td>
                <td>
                    <p>${trip.getArrivalTime()}</p>
                </td>
                <td>
                    <p>${trip.getOrigin()}</p>
                </td>
                <td>
                    <p>${trip.getDestination()}</p>
                </td>
                <td>
                    <p>${trip.getPrice()}</p>
                </td>
                <td>
                    <p>${trip.getCapacity()}</p>
                </td>
            </tr>
            </c:forEach>
            </tr>
        </table>
    </form>
    <form action="${pageContext.request.contextPath}/secured/searchTrips" method="post">
        <table border="1" width="auto" style = "float: left">
            <th>Buy</th>
            <tr>
                <c:forEach var="trip" items="${trips}">
            <tr>
                <td>
                    <button name="tripId" value="${trip.getId()}">Escolher lugar</button>
                </td>
            </tr>
            </c:forEach>
            </tr>
        </table>
    </form>
</div>
</div>
</body>
</html>