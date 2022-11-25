<%@ page pageEncoding="UTF-8" %>
<%@ page session="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>
<%@ page import="ru.sir.richard.boss.model.types.OrderAmountTypes" %>
<%@ page import="ru.sir.richard.boss.model.types.CustomerTypes" %>
<%@ page import="ru.sir.richard.boss.model.types.SupplierTypes" %>
<%@ page import="ru.sir.richard.boss.model.types.CrmTypes" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="ru_RU" />
<fmt:setBundle basename="messages.messages" />

<spring:url value="/resources/client/" var="urlResources"/>
<spring:url value="/" var="urlHome"/>
<spring:url value="/orders" var="urlOrders"/>
<spring:url value="/wiki" var="urlWiki"/>
<spring:url value="/report" var="urReport"/>
<spring:url value="/alarm" var="urlAlarm"/>

<spring:url value="/wiki-rest" var="urlWikiRest"/>
