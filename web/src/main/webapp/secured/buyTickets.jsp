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
<form action="buyTickets" method="get">
  <table border="1" width="auto" style = "float: left">
    <th>Seat Number</th>
    <tr>
      <c:forEach var="tck" items="${availableTcks}">
    <tr>
      <td>
        <p>${tck.getSeatNumber()}</p>
      </td>
    </tr>
    </c:forEach>
    </tr>
  </table>
</form>
<form action="buyTickets" method="post">
  <table border="1" width="auto" style = "float: left">
    <th>buy</th>
    <tr>
      <c:forEach var="tck" items="${availableTcks}">
    <tr>
      <td>
        <button name="tckId" value="${tck.getId()}">Comprar</button>
      </td>
    </tr>
    </c:forEach>
    </tr>
  </table>
</form>
  <a href="${pageContext.request.contextPath}/logout">Logout</a>
  <a href="${pageContext.request.contextPath}/secured/searchTrips">GO BACK</a>
</div>

</body>
</html>