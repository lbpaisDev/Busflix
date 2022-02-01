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
<body>

<form action="home" method="get">
    <div>
        <h4 id="balance">Your actual balance: ${client.getWallet().getBalance()}</h4>
        <h4>Your address: ${client.getAddress()}</h4>
        <h4>Member since: ${client.getCreationDate()}</h4>
        <a href="${pageContext.request.contextPath}/secured/searchTrips">Pesquisar Viagens</a>
        <br>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
        <br>
        <a href="${pageContext.request.contextPath}/secured/clientProfile.jsp">Perfil</a>
    </div>
    <div>
        <table border="1" width="auto" style="float: left">
            <th>Trip Departure Time</th>
            <th>Trip Arrival Time</th>
            <th>Origin Location</th>
            <th>Destination Location</th>
            <th>Ticket Price</th>
            <c:forEach var="tck" items="${cTickets}">
                <tr>
                    <td align="center">
                        <p>${tck.getTrip().getDepartureTime()}</p>
                    </td>
                    <td align="center">
                        <p>${tck.getTrip().getArrivalTime()}</p>
                    </td>
                    <td align="center">
                        <p>${tck.getTrip().getOrigin()}</p>
                    </td>
                    <td align="center">
                        <p>${tck.getTrip().getDestination()}</p>
                    </td>
                    <td align="center">
                        <p>${tck.getTrip().getPrice()} €</p>
                    </td>
                </tr>
            </c:forEach>
            </tr>
        </table>
    </div>
</form>
        <table border="1" width="auto" style="float: left">
            <th>Ticket Seat Number</th>
            <th>Ticket Refund</th>
            <c:forEach var="tck" items="${cTickets}">
            <tr>
                <td align="center">
                    <p>${tck.getSeatNumber()}</p>
                </td>
                <td align="center">
                <form action="${pageContext.request.contextPath}/secured/home" method="post">
                    <button name="refundId" value="${tck.getId()}" type="submit">REFUND</button>
                </form>
                </td>
            </tr>
            </c:forEach>
        </table>
</body>
</html>