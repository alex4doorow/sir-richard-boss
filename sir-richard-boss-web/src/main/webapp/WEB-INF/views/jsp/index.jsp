<%@ include file = "fragments/header2init.jsp" %>
<%@ include file = "fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /></title>

<%@ include file = "fragments/header2html2navbar2start.jsp" %>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2"><fmt:message key="index.header" />:&nbsp;${pageContext.request.userPrincipal.name}</h1>

	<sec:authorize access="isAuthenticated()">
    	<p><a href="${urlHome}logout">Logout</a></p>
	</sec:authorize> 
		
	
</div> 
  
<!-- alert block ets -->

<c:if test="${not empty msg}">		
			<div class="alert alert-${css} alert-dismissible fade show" role="alert">	
				${msg}
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>				
			</div>	
</c:if>	
		<br/>
		
	<security:authorize access="hasRole('ROLE_USER')">
		<p>This text is only visible to a user</p>
		<br/>
	</security:authorize>
	
	<security:authorize access="hasRole('ROLE_ADMIN')">
		<p>This text is only visible to an admin</p>
		<br/>
	</security:authorize>
 
          <div class="table-responsive">
           
            
<%@ include file = "fragments/header2html2navbar2finish.jsp" %>               
            
</div>

<div class="container-fluid">
	<div class="row">
		  		<div class="col-sm-9">
		  		</div>
   				<div class="col-sm-3">
   					<!--  -->
   				</div>
	</div>
</div>


   
<%@ include file = "fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	$('#nav-link-home').addClass('active');
	$('#nav-link-home i').removeClass('text-dark').addClass('text-info');
</script>  	 
  
<%@ include file = "fragments/footer2html.jsp" %>