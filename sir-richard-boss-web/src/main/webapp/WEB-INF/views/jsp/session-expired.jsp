<%@ include file = "fragments/header2init.jsp" %>
<html>
<head>
  	<title>Session Expired Page</title>
	<meta charset="utf-8">    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="<fmt:message key="app.header.description"/>">
    <meta name="keywords" content="<fmt:message key="app.header.keywords"/>"/>
    <meta name="author" content="<fmt:message key="app.header.author"/>">
    <link rel="icon" href="${urlResources}51.ico">
    <!-- Bootstrap core CSS -->
    <link href="${urlResources}css/bootstrap.css" rel="stylesheet">
    <link href="${urlResources}css/bootstrap-icons.css" rel="stylesheet">
    
    <!-- Custom styles for this template -->
    <link href="${urlResources}css/dashboard.css" rel="stylesheet">
    <link href="${urlResources}css/bootstrap-datepicker.css" rel="stylesheet">
    <link href="${urlResources}css/datatables.css" rel="stylesheet"/>
</head>

<body>
	<h1>Session Expired Page</h1>
	<a href="<c:url value="login" />">To Login</a>
</body>
</html>