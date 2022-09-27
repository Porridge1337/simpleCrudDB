<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New user</title>
</head>
<body>
<div>
    <h2>
        <c:if test="${user != null}"> Edit user</c:if>
        <c:if test="${user == null}"> Add new user </c:if>
    </h2>
</div>
<div>
    <c:if test="${user!=null}">
    <form name="myForm" action="/edit" method="post">
        <!--onsubmit="return validateForm()"-->
        </c:if>
        <c:if test="${user==null}">
        <form name="myForm" action="/insert" method="post">
            <!--onsubmit="return validateForm()"-->
            </c:if>
            <c:if test="${user!=null}">
                <input type="hidden" name="id" value="<c:out value="${user.id}"/>">
            </c:if>
            <div>
                <input type="text" name="name" value="<c:out value="${user.name}"/>" id="name">
                <label for="name"> Name </label>
            </div>
            <div>
                <input type="text" name="surname" value="<c:out value="${user.surname}"/>" id="surname">
                <label for="surname"> Surname </label>
            </div>
            <div>
                <c:choose>
                    <c:when test="${user!=null}">
                        <input type="number" name="age" value="<c:out value="${user.age}"/>" id="age"/>
                    </c:when>
                    <c:otherwise>
                        <input type="number" name="age" value="<c:out value="1"/>" id="age"/>
                    </c:otherwise>
                </c:choose>
                <label for="age">Age</label>
            </div>
            <input type="submit" value="save"/>
        </form>
</div>
</body>
</html>
