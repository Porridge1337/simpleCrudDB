<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Users</title>
</head>
<body>
<div>
    <table>
        <thead>
        <tr>
            <th>No</th>
            <th>Name</th>
            <th>Surname</th>
            <th>Age</th>
            <th>Role</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:set var="count" value="0" scope="page"/>
        <c:forEach var="user" items="${requestScope.listUsers}">
            <c:set var="count" value="${count + 1}" scope="page"/>
            <tr>
                <td>
                    <c:out value="${count}"/>
                </td>
                <td>
                    <c:out value="${user.name}"/>
                </td>
                <td>
                    <c:out value="${user.surname}"/>
                </td>
                <td>
                    <c:out value="${user.age}"/>
                </td>
                <td>
                    <c:out value="${user.roleList.get(0).role}"/>
                </td>
                <td>
                    <a href="/delete?id=<c:out value='${user.id}'/>">Delete</a>
                </td>
                <td>
                    <a href="/update?id=<c:out value='${user.id}'/>">Update</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
        <a href="/new">Create new user</a>
    </table>
</div>
</body>
</html>
