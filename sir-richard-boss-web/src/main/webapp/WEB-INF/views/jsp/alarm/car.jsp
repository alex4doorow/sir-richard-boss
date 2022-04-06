<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="alarm.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="alarm.header" />
	</h1>
	
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
		

		<div>				
			    <button id="button-alarm-on" class="btn btn-sm btn-light" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="alarm.btn.action.on"/>'>
			   		<i class="bi bi-sunrise text-dark"></i>
				</button>
				<button id="button-alarm-off" class="btn btn-sm btn-light" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="alarm.btn.action.off"/>'>
			   		<i class="bi bi-sunset text-dark"></i>	   		
				</button>
				<button id="button-alarm-state" class="btn btn-sm btn-light" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="alarm.btn.action.state"/>'>
			   		<i class="bi bi-activity text-dark"></i>
				</button>
				<button id="button-alarm-invasion" class="btn btn-sm btn-danger" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="alarm.btn.action.invasion"/>'>
			   		<i class="bi bi-bullseye text-dark"></i>
				</button>
			    		
		</div>
		<br/>
		

		
		<div class="alert alert-${classState} alert-dismissible fade show" role="alert">
		  <strong>${headerState}</strong>&nbsp;${messageState}
		  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
		    <span aria-hidden="true">&times;</span>
		  </button>
		</div>

		
		
 
          <div class="table-responsive">
            <table class="table table-striped table-sm">
	            <thead>
					<tr>						
						<th style="width: 5%" scope="col"><fmt:message key="alarm.table.headers.no" /></th>							
						<th style="width: 10%" scope="col"><fmt:message key="alarm.table.headers.dateAdded" /></th>
						<th scope="col"><fmt:message key="alarm.table.headers.message" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="message" items="${messages}">			
					
					<c:choose>         
				         <c:when test = "${Integer.valueOf(message.code) == 3}">
				    <tr class="table table-danger">
				         </c:when>
				         <c:otherwise>				       
				    <tr class="table">    
				         </c:otherwise>
				    </c:choose>				    
			 				<td>${message.id}</td>
							<td><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${message.addedDate}" /></td>
							<td>${message.message}</td>
														
					</tr>			
					</c:forEach>
		         </tbody>
	         </table>
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>

<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
$('#nav-link-alarm').addClass('active');
$('#nav-link-alarm i').removeClass('text-dark').addClass('text-info');

$('#button-alarm-on').click(function() {		
	//actionExecute("1");
	window.location = '${urlAlarm}/car/action/1';	
});

$('#button-alarm-off').click(function() {
	//actionExecute("2");
	window.location = '${urlAlarm}/car/action/2';	
});

$('#button-alarm-state').click(function() {		
	window.location = '${urlAlarm}/car/action/3';	
});

$('#button-alarm-invasion').click(function() {
	//actionExecute("101");
	window.location = '${urlAlarm}/car/action/101';	
});

function actionExecute(actionValue) {		
	
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: '${urlHome}ajax/alarm/car/action/' + actionValue,
		data: JSON.stringify(""),
		dataType: 'json',
		timeout: 100000,
		success: function(data) {
			console.log('SUCCESS: ', data);
			display(data);
						
		},
		error: function(e) {
			console.log('ERROR: ', e);
			display(e);

		},
		done: function(e) {
			console.log('DONE');				
		}
	});			
}

function display(data) {
	/*
	var json = '<h4>Ajax Response</h4><pre>'
			+ JSON.stringify(data, null, 4) + '</pre>';
	$('#feedback').html(json);
	*/
}
</script>   
 
  
<%@ include file = "../fragments/footer2html.jsp" %>


