<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="orders.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="orders.header" />
	</h1>

	<div class="btn-toolbar mb-2 mb-md-0">
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary">Share</button>
			<button class="btn btn-sm btn-outline-secondary" onclick="onClickConditionsFilter()"><fmt:message key="orders.conditions.btn.filter" /></button>
		</div>
		<button id="button-conditions" type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle" data-toggle="dropdown" data-action="no-action" aria-haspopup="true" aria-expanded="false">
			<span data-feather="calendar"></span>&nbsp;${reportPeriodType.annotation.substring(0, 1).toUpperCase()}${reportPeriodType.annotation.substring(1)}
			 
		</button>
		<div class="dropdown-menu" aria-labelledby="button-conditions">
			      <a href="${urlOrders}/conditions/period/current-month" id="button-conditions-current-month" class="dropdown-item"><fmt:message key="orders.conditions.btn.currentMonth" /></a>
			      <a href="${urlOrders}/conditions/period/prior-month" id="button-conditions-prior-month" class="dropdown-item"><fmt:message key="orders.conditions.btn.priorMonth" /></a>
			      <a href="${urlOrders}/conditions/period/last-7-days" id="button-conditions-last-7-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last7Days" /></a>
			      <a href="${urlOrders}/conditions/period/last-30-days" id="button-conditions-last-30-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last30Days" /></a>
			      <a href="${urlOrders}/conditions/period/last-90-days" id="button-conditions-last-90-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last90Days" /></a>
		</div>
	</div>
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
			    <button id="button-crm-load" class="btn btn-sm btn-light" onclick="onClickCrmLoad()" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="orders.btn.crm-load"/>'>
			   		<i class="bi bi-arrow-repeat text-dark"></i>
				</button>
				<button id="button-statuses-reload" class="btn btn-sm btn-light" onclick="onClickStatusesReload()" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="orders.btn.statuses-reload"/>' >
					<i class="bi bi-activity text-dark"></i>
				</button>
				<button id="button-statuses-today" class="btn btn-sm btn-light" onclick="onClickStatusesToday()" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="orders.btn.statuses-today"/>' >
			   		<i class="bi bi-calendar-date text-dark"></i>
				</button>				
				<button id="button-orders-trouble" class="btn btn-sm btn-light" onclick="onClickTroubleOrdersLoad()" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="orders.btn.orders-trouble"/>'>
					<i class="bi bi-exclamation-triangle text-dark"></i>
				</button>			
			    
			    <button id="button-add" type="button" class="btn btn-sm btn-light" onclick="onClickAddOrder()"><fmt:message key="main.btn.add" /></button>
			    <button id="button-update" type="button" class="btn btn-sm btn-light" disabled onclick="onClickUpdateOrder()"><fmt:message key="main.btn.update" /></button>
			    <button id="button-erase" type="button" class="btn btn-sm btn-danger" disabled onclick="onClickEraseOrder()"><fmt:message key="main.btn.delete"/></button>
		
				<div class="btn-group" role="group">
				
					<button id="button-operates" type="button" class="btn btn-sm btn-light dropdown-toggle" disabled data-toggle="dropdown" data-action="no-action" aria-haspopup="true" aria-expanded="false">
				      <fmt:message key="orders.btn.operates" />
				    </button>
				    <div class="dropdown-menu" aria-labelledby="button-operates">
				      <!-- <a id="button-approve" class="dropdown-item"><fmt:message key="orders.btn.approve" /></a>  -->
				      <!-- <a id="button-cancel" class="dropdown-item"><fmt:message key="orders.btn.cancel" /></a> -->
				      <a id="button-change-status" class="dropdown-item"><fmt:message key="orders.btn.changeStatus" /></a>
				      <a id="button-bill-expired-status" class="dropdown-item"><fmt:message key="orders.btn.bidExpired" /></a>
				      <div class="dropdown-divider"></div>				      				      				      
				      <a id="button-clone" class="dropdown-item"><fmt:message key="orders.btn.clone" /></a>
				      
				    </div>
			    </div>
				
			    <div class="btn-group" role="group">
				    <button id="button-reportes" type="button" class="btn btn-sm btn-light dropdown-toggle" disabled data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				      <fmt:message key="orders.btn.print" />
				    </button>
				    <div class="dropdown-menu" aria-labelledby="button-reportes">
				      <a id="button-report-bill" class="dropdown-item"><fmt:message key="orders.btn.print.bill" /></a>
				      <a id="button-report-sdek-bill" class="dropdown-item"><fmt:message key="orders.btn.print.sdekBill" /></a>
				      
				      <div class="dropdown-divider"></div>				      
				      <a id="button-report-post-russia-postpay" class="dropdown-item"><fmt:message key="orders.btn.print.postRussiaPostpay" /></a>
				      <a id="button-report-post-russia-address-ticket-postpay" class="dropdown-item"><fmt:message key="orders.btn.print.postRussiaAddressTicketWithPostpay" /></a>				      
				      <a id="button-report-post-russia-address-ticket" class="dropdown-item"><fmt:message key="orders.btn.print.postRussiaAddressTicket" /></a>

				  	  <div class="dropdown-divider"></div>
					  <a id="button-report-kkm" class="dropdown-item"><fmt:message key="orders.btn.print.kkm" /></a>				  	  
					  <a id="button-report-pko" class="dropdown-item"><fmt:message key="orders.btn.print.pko" /></a>
				      
				      <div class="dropdown-divider"></div>				      
				      <a id="button-report-garant-ticket-all" class="dropdown-item"><fmt:message key="orders.btn.print.garantTicketAll" /></a>
				      <a id="button-report-garant-ticket-sititek" class="dropdown-item"><fmt:message key="orders.btn.print.garantTicketSititek" /></a>
				      
   				  	  <div class="dropdown-divider"></div>
					  <a id="button-report-return-form" class="dropdown-item"><fmt:message key="orders.btn.print.returnForm" /></a>				  	  
				      				      
				    </div>
			    </div>
			    
			    <div class="btn-group" role="group">
				    <button id="button-exporters" type="button" class="btn btn-sm btn-light dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				      <fmt:message key="orders.btn.export" />
				    </button>
				    <div class="dropdown-menu" aria-labelledby="button-exportes">
				      <a id="button-export-excel-cdek" class="dropdown-item" href="${urlOrders}/0/export/excel-cdek"><fmt:message key="orders.btn.export.excel.cdek" /></a>
				      <a id="button-export-api-cdek" class="dropdown-item" href="${urlOrders}/0/export/api-cdek"><fmt:message key="orders.btn.export.api.cdek" /></a>
				      <a id="button-export-api-ozon-rocket" class="dropdown-item" href="${urlOrders}/0/export/api-ozon-rocket"><fmt:message key="orders.btn.export.api.ozon.rocket" /></a>
				    </div>
			    </div>			    
			    	    
			    <!--  
			    <button id="button-print" type="button" class="btn btn-sm btn-light" disabled onclick="onClickPrintOrderBill()"><fmt:message key="main.btn.print" /></button>
			    -->
			    
			    		
		</div>
		<br/>
 
          <div class="table-responsive">
            <table class="table table-striped- table-sm">

            
			<tbody>
				<c:forEach var="vacancyPage" items="${vacanciesPages}">
				
					<c:forEach var="vacancy" items="${vacancyPage.vacancies}">
					
						<div class="card" style="width: 100%;">
						  <!-- <img src="..." class="card-img-top" alt="...">  -->
						  <div class="card-body">
						    <h5 class="card-title">${vacancy.id} ${vacancy.name}</h5>
						    <p class="card-text">${vacancy.responsibility}</p>
						    <p class="card-text">${vacancy.requirement}</p>
						    <p class="card-text">${vacancy.areaName}</p>
						    <p class="card-text">${vacancy.published}</p>
						    <p class="card-text">${vacancy.scheduleName}</p>
						    <p class="card-text">${vacancy.employerName}</p>
						    <p class="card-text">${vacancy.vacancyUrl}</p>
						   
						    

						    
						    <a href="#" class="btn btn-primary">Hello anybody!</a>
						  </div>
						</div>
						<br/>								
				
					</c:forEach>
				
				
				
				
							
			
				</c:forEach>
	         </tbody>
	         </table>
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>

   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
		$('#nav-link-orders').addClass('active');
		$('#nav-link-orders i').removeClass('text-dark').addClass('text-info');
		
	</script>   
	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


