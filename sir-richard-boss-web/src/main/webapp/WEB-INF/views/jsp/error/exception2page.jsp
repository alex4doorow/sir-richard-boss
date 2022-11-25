<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>

<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>

<fmt:setLocale value="ru_RU" />
<fmt:setBundle basename="messages.messages" />

<spring:url value="/resources/client/" var="urlResources"/>
<spring:url value="/" var="urlHome"/>

<!doctype html>
<html lang="ru">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="<fmt:message key="app.header.description"/>">
    <meta name="keywords" content="<fmt:message key="app.header.keywords"/>"/>
    <meta name="author" content="<fmt:message key="app.header.author"/>">
    <link rel="icon" href="${urlResources}51.ico">
    <!-- Bootstrap core CSS -->
    <link href="${urlResources}css/bootstrap.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="${urlResources}css/dashboard.css" rel="stylesheet">
    <link href="${urlResources}css/bootstrap-datepicker.css" rel="stylesheet">
  
    <title><fmt:message key="error.title" /></title>

	<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">   
	<!-- local content -->            
	<h1 class="h2">${errorHeader}</h1>
 
	<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>

<div class="container-fluid">
	<div class="row">
		<div class="col-sm-2"></div>	
		<div class="col-sm-9">
			<p>${errorMsg}</p>
		</div>
	</div>
	<div class="row">	
	<div class="col-sm-2">
	</div>
   	<div class="col-sm-9">
		<pre>${stackTrace}</pre>
	 </div>	
	 
<!--
    Failed URL: ${url}
    Exception:  ${exception.message}
        <c:forEach items="${exception.stackTrace}" var="ste">    ${ste} 
    </c:forEach>
-->
	 	
	</div>
</div>
	         
	</div>    
	<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>      
	//$('#nav-link-home').addClass('active');
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>

