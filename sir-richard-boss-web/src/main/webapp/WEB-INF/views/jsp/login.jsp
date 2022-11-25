<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file = "fragments/header2init.jsp" %>

<!DOCTYPE html>
<html>
<head>
  	<title>Log in with your account</title>
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
	<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
		<div class="container-fluid">
			<div class="col-md-8 order-md-1">					
				<div class="row">					
					<form class="needs-validation" name='f' action="${urlHome}login" method='POST'>
						<h1 class="h2 mb-3">Login</h1>
						<hr class="mb-4">
					  	<div class="col-md form-group">
					    	<label for="input-login-name" class="form-label">User</label>
					    	<input type="text" class="form-control" id="input-login-name" name="username" placeholder="Введите логин">
					    	<small id="input-login-name-help" class="text-muted form-text">
						  		Мы никогда никому не передадим Ваши данные.
							</small>
					  	</div>
					  	<div class="col-md form-group">		  
					    	<label for="input-login-password" class="form-label">Password</label>
					    	<input type="password" class="form-control" id="input-login-password" name="password" placeholder="Введите пароль">
					    	<small id="input-login-name-help" class="text-muted form-text">
						  		Ваш пароль должен состоять из 8-20 символов, содержать буквы и цифры<br> и не должен содержать пробелов, специальных символов или эмодзи.
							</small>
					  	</div>
					  	<div class="col-md form-group">
					  		<button type="submit" class="btn btn-primary">Вход</button>				
					  	</div>
					</form>
				</div>
				<div class="row">
					  <label class="col-sm-12"></label>
			  		  <div>© 2022 Copyright:<a target="blank" href="https://pribormaster.ru/">&nbsp;www.pribormaster.ru</a>&nbsp;|&nbsp;<spring:eval expression="@mvcWebConfig.applicationName"/>&nbsp;|&nbsp;<spring:eval expression="@mvcWebConfig.applicationVersion"/></div>
				</div>  
			</div>
			<hr>	
		</div>
	</main>
</body>
<footer class="page-footer font-small gray-dark pt-12">

</footer>
	
<!-- 
	<form name='f' action="${urlHome}login" method='POST'>
		<table>
			<tr>
				<td>User:</td>
				<td><input type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td>Remember Me:</td>
				<td><input type="checkbox" name="remember-me" /></td>
			</tr>
			<tr>
				<td><input name="submit" type="submit" value="submit" /></td>
			</tr>
		</table>
	</form>
 -->

</html>