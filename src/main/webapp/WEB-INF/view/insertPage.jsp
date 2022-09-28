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
                <!-- <input type="hidden" name="r_id" value="<c:out value="${user.roleList.get(0).r_id}"/>">-->
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
            <c:if test="${user==null}">
                <select id="role" name="role">
                    <c:forEach items="${roleList}" var="roles">
                        <option value="${roles.r_id}"> ${roles.role}</option>
                    </c:forEach>
                </select>
            </c:if>
            <c:if test="${user!=null}">
                <div>
                    <label for="role"><c:out value="${user.roleList.get(0).role}"/></label>
                </div>
                <select id="role" name="role">
                    <c:forEach items="${roleList}" var="roles">
                        <option value="${roles.r_id}"> ${roles.role}</option>
                    </c:forEach>
                </select>
            </c:if>
        </form>
</div>
</body>
</html>
