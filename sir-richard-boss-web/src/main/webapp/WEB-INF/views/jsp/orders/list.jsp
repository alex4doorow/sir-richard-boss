<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="orders.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
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
				<button id="button-orders-actualization-postpay" class="btn btn-sm btn-light" onclick="onClickActualizationOrdersPostay()" data-toggle="tooltip" data-placement="bottom" title='<fmt:message key="orders.btn.actualization-postpay"/>'>
					<i class="bi bi-cash-stack text-dark"></i>
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

            <thead>
				<tr>
					<th scope="col"><input id="checkbox-union" type="checkbox"></th>
					<th scope="col"><fmt:message key="orders.table.headers.no" /></th>							
					<th scope="col"><fmt:message key="orders.table.headers.dateOrder" /></th>
					<th scope="col"><fmt:message key="orders.table.headers.category" /></th>
					<th scope="col"><fmt:message key="orders.table.headers.client" /></th>		
					<th scope="col"><fmt:message key="orders.table.headers.phone" /></th>
					<th scope="col"><fmt:message key="orders.table.headers.address" /></th>
					<th scope="col"><fmt:message key="orders.table.headers.delivery" /></th>				
					<th scope="col"><fmt:message key="orders.table.headers.amount" /></th>					
					<th scope="col"><fmt:message key="orders.table.headers.trackCode" /></th>				
				</tr>
			</thead>
			<tbody>
				<c:forEach var="order" items="${orders}">			
				<tr class="table-${order.viewStatus.union}">				
		 				<td class="table-${order.viewStatus.orderId}"><input data-delivery-code="${order.delivery.deliveryType}" data-delivery-category="${order.delivery.deliveryType.category}" id="checkbox-${order.id}" type="checkbox" data-no="${order.no}"></td>
						<td id="td-id-${order.id}" class="td-id table-${order.viewStatus.orderNo}" scope="row" data-id="${order.id}"><a href="${urlOrders}/${order.id}/${listType}">${order.no}</a></td>							
						
						<td class="table-${order.viewStatus.orderDate}" data-original-title="${order.viewDateInfo}" data-container="body" data-toggle="tooltip" data-placement="bottom" title="">
							<fmt:formatDate pattern="dd.MM.yyyy" value="${order.orderDate}" />
						</td>						
						<td>${order.productCategory.name}</td>						
						<td data-original-title="${order.annotation}" data-toggle="tooltip" data-placement="bottom" title="">${order.customer.viewShortName}</td>
						<td>${order.customer.viewPhoneNumber}</td>
						<td data-original-title="${order.delivery.viewDeliveryInfo}" data-toggle="tooltip" data-placement="bottom" title="">${order.delivery.address.viewAddress}</td>
						<td>${order.delivery.deliveryType.annotation}</td>
						<td class="text-right" 
							data-original-title="<fmt:message key="orders.table.items.amount.bill" />: <fmt:formatNumber type = 'currency' value = '${order.amounts.getValue(OrderAmountTypes.BILL)}' />, 
								<fmt:message key="orders.table.items.amount.supplier" />: <fmt:formatNumber type = 'currency' value = '${order.amounts.getValue(OrderAmountTypes.SUPPLIER)}' />, 
								<fmt:message key="orders.table.items.amount.margin" />: <fmt:formatNumber type = 'currency' value = '${order.amounts.getValue(OrderAmountTypes.MARGIN)}' />, 
								<fmt:message key="orders.table.items.amount.postpay" />: <fmt:formatNumber type = 'currency' value = '${order.amounts.getValue(OrderAmountTypes.POSTPAY)}' />" 
								data-container="body" data-toggle="tooltip" data-placement="bottom" title="">				
						  <fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.TOTAL_WITH_DELIVERY)}" />					
						<td id="td-trackcode-${order.id}" class="text-center">${order.delivery.trackCode}</td>
						
							
				</tr>			
				</c:forEach>
	         </tbody>
	         </table>
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>

<div class="container-fluid">
	<div class="row">
		  		<div class="col-sm-9">
		  		</div>
   				<div class="col-sm-3">
   				
	   				<ul class="list-group-flush">
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.amount.bill" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${totalAmounts.get(OrderAmountTypes.BILL)}" /></strong></li>
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.amount.supplier"/>&nbsp;<strong><fmt:formatNumber type = "currency" value = "${totalAmounts.get(OrderAmountTypes.SUPPLIER)}" /></strong></li>
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.amount.advert"/>&nbsp;<strong><fmt:formatNumber type = "currency" value = "${totalAmounts.get(OrderAmountTypes.ADVERT_BUDGET)}" /></strong></li>
					    
					    
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.amount.margin" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${totalAmounts.get(OrderAmountTypes.MARGIN)}" /></strong></li>
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.amount.postpay" />&nbsp;
				         	<strong 
				         		data-container="body" data-toggle="tooltip" data-placement="bottom" title="" 
				         		data-original-title="<fmt:message key='orders.table.footers.amount.postpaySDEK' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_SDEK)}' />, 
				         							 <fmt:message key='orders.table.footers.amount.postpayPost' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_POST)}' />,
				         							 <fmt:message key='orders.table.footers.amount.postpayCompany' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_COMPANY)}' />
				         							 <fmt:message key='orders.table.footers.amount.postpayYandexMarket' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_YANDEX_MARKET)}' />
				         							 <fmt:message key='orders.table.footers.amount.postpayOzonMarket' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_OZON_MARKET)}' />
				         							 <fmt:message key='orders.table.footers.amount.postpayOzonRocket' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_OZON_ROCKET)}' />
				         							 <fmt:message key='orders.table.footers.amount.postpayYandexGo' /> <fmt:formatNumber type = 'currency' value = '${totalAmounts.get(OrderAmountTypes.POSTPAY_YANDEX_GO)}' />				         							 
				         							 ">
				         		<fmt:formatNumber type = "currency" value = "${totalAmounts.get(OrderAmountTypes.POSTPAY)}" />
				         	</strong>
				         </li>
					    
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.count.total" />&nbsp;${orders.size()}&nbsp;</li>
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.count.real" />&nbsp;<strong><fmt:formatNumber type = "number" value = "${totalAmounts.get(OrderAmountTypes.COUNT_REAL_ORDERS)}" />&nbsp;</strong></li>
					    <li class="list-group-item text-right"><fmt:message key="orders.table.footers.percent.conversionApproved" />&nbsp;
					    	<strong
					    		data-container="body" data-toggle="tooltip" data-placement="bottom" title="" 
				         		data-original-title="<fmt:message key='orders.table.footers.percent.conversionBid' />&nbsp;<fmt:formatNumber type = 'percent' minFractionDigits = "2" value = '${totalAmounts.get(OrderAmountTypes.CONVERSION_BID)}' />">
				         			<fmt:formatNumber type = "percent" minFractionDigits = "2" value = "${totalAmounts.get(OrderAmountTypes.CONVERSION_APPROVED)}" />&nbsp;
				         	</strong>
				         </li>				    
					</ul>  				
   				
   				</div>
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="confirm-modal-erase" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirm-modal-title-erase"><fmt:message key="order.form.modal.erase.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><fmt:message key="order.form.modal.erase.text" /></p>
      </div>
      <div class="modal-footer">
        <button id="button-modal-confirm-cancel-1" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
        <button id="button-modal-confirm-ok-1" type="button" class="btn btn-primary"><fmt:message key="main.btn.ok" /></button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="confirm-modal-actualization-postpay" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirm-modal-title-actualization-postpay"><fmt:message key="order.form.modal.orders.actualization.postpay.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><fmt:message key="order.form.modal.orders.actualization.postpay.text" /></p>
      </div>
      <div class="modal-footer">
        <button id="button-modal-confirm-cancel-2" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
        <button id="button-modal-confirm-ok-2" type="button" class="btn btn-primary"><fmt:message key="main.btn.ok" /></button>
      </div>
    </div>
  </div>
</div>
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
		$('#nav-link-orders').addClass('active');
		$('#nav-link-orders i').removeClass('text-dark').addClass('text-info');
				
		function onClickCrmLoad() {
			window.location = '${urlOrders}/import-crm';			
		}
		
		function onClickStatusesReload() {
			window.location = '${urlOrders}/statuses/reload';			
		}
		
		function onClickStatusesToday() {
			window.location = '${urlOrders}/statuses/today';			
		}
		
		function onClickTroubleOrdersLoad() {
			window.location = '${urlOrders}/trouble-load';			
		}
		
		function onClickActualizationOrdersPostay() {
			$('#confirm-modal-actualization-postpay').modal({keyboard: false});				
		}
		
		function onClickConditionsFilter() {
			window.location = '${urlOrders}/conditions/filter';			
		}		
		function onClickAddOrder() {
			console.log('${urlOrders}/add');			
			window.location = '${urlOrders}/add/${listType}';
		}
		function onClickUpdateOrder() {
			console.log('onClickUpdateOrder: ' + $('#button-update').attr('href'));
			window.location = $('#button-update').attr("href");			
		}		
		function onClickEraseOrder() {
			console.log('onClickEraseOrder: ' + $('#button-erase').attr('href-data'));
			$('#confirm-modal-erase').modal({keyboard: false});
		}
		function onClickApproveOrder() {
			console.log('onClickApproveOrder: ' + $('#button-approve').attr('href'));
			window.location = $('#button-approve').attr("href");
		}
		function onClickCancelOrder() {
			console.log('onClickCancelOrder: ' + $('#button-cancel').attr('href'));
			window.location = $('#button-cancel').attr("href");		
		}			
		function onClickChangeStatusOrder() {
			console.log('onClickChangeStatusOrder: ' + $('#button-change-status').attr('href'));
			//this.disabled = true;
			window.location = $('#button-change-status').attr("href");	
		}
		
		function onClickBillExpiredStatus() {
			console.log('onClickBillExpiredStatus: ' + $('#button-bill-expired-status').attr('href'));			
			window.location = $('#button-bill-expired-status').attr("href");	
		}
				
		function onClickPrintOrderBill() {
			console.log('onClickPrintOrderBill: ' + $('#button-report-bill').attr('href'));
			window.location = $('#button-report-bill').attr("href");			
		}
		function onClickPrintOrderKKM() {
			console.log('onClickPrintOrderKKM: ' + $('#button-report-kkm').attr('href'));
			window.location = $('#button-report-kkm').attr("href");			
		}
		function onClickPrintOrderPKO() {
			console.log('onClickPrintOrderPKO: ' + $('#button-report-pko').attr('href'));
			window.location = $('#button-report-pko').attr("href");			
		}			
		function onClickPrintOrderSdekBill() {
			console.log('onClickPrintOrderSdekBill: ' + $('#button-report-sdek-bill').attr('href'));
			window.location = $('#button-report-sdek-bill').attr("href");			
		}
		function onClickPrintOrderPostRussiaAddressTicket() {
			console.log('onClickPrintOrderPostRussiaAddressTicket: ' + $('#button-report-post-russia-address-ticket').attr('href'));
			window.location = $('#button-report-post-russia-address-ticket').attr("href");			
		}
		function onClickPrintOrderPostRussiaAddressTicketWithPostpay() {
			console.log('onClickPrintOrderPostRussiaAddressTicketWithPostpay: ' + $('#button-report-post-russia-address-ticket-postpay').attr('href'));
			window.location =  $('#button-report-post-russia-address-ticket-postpay').attr("href");			
		}
		function onClickPrintOrderPostRussiaPostpay() {
			console.log('onClickPrintOrderPostRussiaPostpay: ' + $('#button-report-post-russia-postpay').attr('href'));
			window.location = $('#button-report-post-russia-postpay').attr("href");			
		}
		function onClickPrintGarantTicketAll() {
			console.log('onClickPrintGarantTicketAll: ' + $('#button-report-garant-ticket-all').attr('href'));
			window.location = $('#button-report-garant-ticket-all').attr("href");			
		}
		function onClickPrintGarantTicketAll() {
			console.log('onClickPrintGarantTicketSititek: ' + $('#button-report-garant-ticket-sititek').attr('href'));
			window.location = $('#button-report-garant-ticket-sititek').attr("href");			
		}
		
		function onClickPrintOrderReturnForm() {
			console.log('onClickPrintReturnForm: ' + $('#button-report-return-form').attr('href'));
			window.location = $('#button-report-return-form').attr("href");			
		}
		
		function onClickExportCdekExcel() {
			console.log('onClickExpotCdekExcel: ' + $('#button-export-excel-cdek').attr('href'));
			window.location = $('#button-export-excel-cdek').attr("href");			
		}
		
		function onClickExportCdekApi() {
			console.log('onClickExpotCdekApi: ' + $('#button-export-api-cdek').attr('href'));
			window.location = $('#button-export-api-cdek').attr("href");			
		}
		
		function onClickExportOzonRocketApi() {
			console.log('onClickExportOzonRocketApi: ' + $('#button-export-api-ozon-rocket').attr('href'));
			window.location = $('#button-export-api-ozon-rocket').attr("href");			
		}
				
		$(document).ready(function() {
			var $checkboxes = $('input[type="checkbox"]');
			$checkboxes.click(function() {
				var orderId,
					updateHref,
					deleteHref,
					cloneHref,
					approveHref,
					cancelHref,
					changeStatusHref,
					bidExpiredHref,
					eraseHref,
					reportOrderBillHref,
					reportOrderSdekBillHref,
					reportPostRussiaAddressTicketHref,
					reportPostRussiaAddressTicketPostpayHref,
					reportPostRussiaPostpayHref,
					reportGarantTicketAllHref,
					reportGarantTicketSititekHref,
					reportOrderKKMHref,
					reportOrderPKOHref,
					reportOrderReturnFormHref,
					exportExcelCdekHref,
					exportApiCdekHref,
					exportApiOzonRocketHref;
					
				if (this.id == 'checkbox-union') {
					console.log('checkbox-union: ' + $('#checkbox-union').prop('checked')); 
					$checkboxes.prop('checked', $('#checkbox-union').prop('checked'));
					
					$('#button-update').attr('disabled', '').attr('href', '#');        	    	
    				$('#button-delete').attr('disabled', '').attr('href', '#');    				
    				$('#button-clone').attr('disabled', '').attr('href', '#');
    				$('#button-operates').attr('disabled', '');    				
    				$('#button-reportes').attr('disabled', '');
    				
    				exportExcelCdekHref = '${urlOrders}/' + 0 + '/export/excel-cdek';
    				exportApiCdekHref = '${urlOrders}/' + 0 + '/export/api-cdek';
    				exportApiOzonRocketHref = '${urlOrders}/' + 0 + '/export/api-ozon-rocket';
    				
    				$('#button-export-excel-cdek').attr('href', exportExcelCdekHref);    				
    				$('#button-export-api-cdek').attr('href', exportApiCdekHref);
    				$('#button-export-api-ozon-rocket').attr('href', exportApiOzonRocketHref);
    				
					return;
				}
				if ($checkboxes.filter(':checked').length == 0) {
					$('#button-update').attr('disabled', '').attr('href', '#');        	    	
    				$('#button-delete').attr('disabled', '').attr('href', '#');
    				$('#button-clone').attr('disabled', '').attr('href', '#');    				
    				$('#button-operates').attr('disabled', '');    				
    				$('#button-reportes').attr('disabled', '');
    				//$('#button-exporters').attr('disabled', '');
				}	
				
				$checkboxes.filter(':checked').not(this).prop('checked', false);
				
				if ($(this).prop('checked')) {
					
					var deliveryCode = $(this).attr('data-delivery-code');
					var deliveryCategory = $(this).attr('data-delivery-category');
					
					$('#button-exporters').attr('disabled', '');
					$('#button-export-excel-cdek').prop('hidden', true);
					$('#button-export-api-cdek').prop('hidden', true);					
					$('#button-export-api-ozon-rocket').prop('hidden', true);					
					if (deliveryCategory == "OZON Rocket") {
						$('#button-exporters').removeAttr('disabled');    
						$('#button-export-api-ozon-rocket').prop('hidden', false);			
					} else if (deliveryCategory == "CDEK" || deliveryCode == "PICKUP") {
						$('#button-exporters').removeAttr('disabled');    
						$('#button-export-excel-cdek').prop('hidden', false);
						$('#button-export-api-cdek').prop('hidden', false);		
					}
					
    				orderId = this.id.substring(9, this.id.length);    				
        	    	updateHref = '${urlOrders}/' + orderId + '/update/${listType}';        	    	        	    	
        	    	cloneHref = '${urlOrders}/' + orderId + '/clone';
        	    	deleteHref = '${urlOrders}/' + orderId + '/delete';
        	    	cloneHref = '${urlOrders}/' + orderId + '/clone';        	    	
        	    	
        	    	changeStatusHref = '${urlOrders}/' + orderId + '/change-status/${listType}';
        	    	bidExpiredHref = '${urlOrders}/' + orderId + '/bill-expired-status/${listType}';
        	    	eraseHref = '${urlOrders}/' + orderId + '/erase';
        	    	
        	    	reportOrderBillHref = '${urlOrders}/' + orderId + '/report/bill';
        	    	reportOrderSdekBillHref = '${urlOrders}/' + orderId + '/report/sdek-bill';
        	    	reportPostRussiaAddressTicketHref = '${urlOrders}/' + orderId + '/report/post-russia-address-ticket';
        	    	reportPostRussiaAddressTicketPostpayHref = '${urlOrders}/' + orderId + '/report/post-russia-address-ticket-postpay';
        	    	reportPostRussiaPostpayHref = '${urlOrders}/' + orderId + '/report/post-russia-postpay';
        	    	
        	    	reportGarantTicketAllHref = '${urlOrders}/' + orderId + '/report/garant-ticket-all';
        	    	reportGarantTicketSititekHref = '${urlOrders}/' + orderId + '/report/garant-ticket-sititek';
        	    	
        	    	reportOrderKKMHref = '${urlOrders}/' + orderId + '/report/kkm';
        	    	reportOrderPKOHref = '${urlOrders}/' + orderId + '/report/pko';
        	    	
        	    	reportOrderReturnFormHref = '${urlOrders}/' + orderId + '/report/return-form';
        	    	
        	    	exportExcelCdekHref = '${urlOrders}/' + orderId + '/export/excel-cdek';
        	    	exportApiCdekHref = '${urlOrders}/' + orderId + '/export/api-cdek';
        	    	exportApiOzonRocketHref = '${urlOrders}/' + orderId + '/export/api-ozon-rocket';
        	    	        	    	      	           	    	        	    	        	    	
        	    	console.log(updateHref);
        	    	console.log(deleteHref);
        	    	console.log(changeStatusHref);
        	    	console.log(bidExpiredHref);
        	    	console.log(eraseHref);
        	    	console.log(reportOrderBillHref);
        	    	        	    	
        	    	$('#button-update').removeAttr('disabled').attr('href', updateHref);        	    	             	    	
    				$('#button-erase').removeAttr('disabled').attr('href', eraseHref);
    				
    				$('#button-operates').removeAttr('disabled');
    
    				$('#button-change-status').attr('href', changeStatusHref);
    				$('#button-bill-expired-status').attr('href', bidExpiredHref);
    				
    				$('#button-delete').attr('href', deleteHref);
    				$('#button-clone').attr('href', cloneHref);
    				    				
    				$('#button-reportes').removeAttr('disabled');
    				//$('#button-exporters').removeAttr('disabled');    			
    				$('#button-report-bill').attr('href', reportOrderBillHref);
    				$('#button-report-kkm').attr('href', reportOrderKKMHref);
    				$('#button-report-pko').attr('href', reportOrderPKOHref);    				
    				
    				$('#button-report-sdek-bill').attr('href', reportOrderSdekBillHref);
    				$('#button-report-post-russia-address-ticket').attr('href', reportPostRussiaAddressTicketHref);
    				$('#button-report-post-russia-address-ticket-postpay').attr('href', reportPostRussiaAddressTicketPostpayHref);    				
    				$('#button-report-post-russia-postpay').attr('href', reportPostRussiaPostpayHref);
    				
    				$('#button-report-garant-ticket-all').attr('href', reportGarantTicketAllHref);
    				$('#button-report-garant-ticket-sititek').attr('href', reportGarantTicketSititekHref);
    				
    				$('#button-report-return-form').attr('href', reportOrderReturnFormHref);
    				
    				$('#button-export-excel-cdek').attr('href', exportExcelCdekHref);
    				$('#button-export-api-cdek').attr('href', exportApiCdekHref);
    				$('#button-export-api-ozon-rocket').attr('href', exportApiOzonRocketHref);
    				    				
    				    				  		
    			} else {
    				exportExcelCdekHref = '${urlOrders}/' + 0 + '/export/excel-cdek';
    				exportApiCdekHref = '${urlOrders}/' + 0 + '/export/api-cdek';
    				exportApiOzonRocketHref = '${urlOrders}/' + 0 + '/export/api-ozon-rocket';
    			}
    	    });
		});	
		
		$( 'td.td-id' ).mouseover(function() {			
			var orderId = $(this).attr('data-id');
			var tdOrderNo = $(this);			
			var orderContainer = { id: orderId }		
			
			if (tdOrderNo.attr("data-toggle") == "tooltip") {
				//console.log('AJAX YET}: ' + tdOrderNo.attr('title'));	
				return;
			}			
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: '${urlHome}ajax/orders/marketplace-info',
				data : JSON.stringify(orderContainer),
				dataType: 'json',
				timeout: 100000,
				success: function(data) {
					console.log('SUCCESS: ', data.msg);	
					if (data.msg != '') {
						if ($('#td-trackcode-' + orderId).text().trim() == '') {
							$('#td-trackcode-' + orderId).text(data.msg);	
						} 
						tdOrderNo.attr('title', data.msg).attr('data-toggle', 'tooltip').tooltip('show');
						setTimeout(function() {							
							var tdOrderNo = $(this);
							tdOrderNo.tooltip('hide');
							$('.td-id').tooltip('hide');
						}, 4000);
					} else {
						tdOrderNo.attr('title', data.msg).attr('data-toggle', 'tooltip').tooltip('hide');
					}					
				},
				error: function(e) {
					console.log('ERROR: ', e);
					},
				done: function(e) {
					console.log('DONE');					
				}
			});		
		}).mouseout(function() {
			  var tdOrderNo = $(this);
			  tdOrderNo.tooltip('hide');
			  $('.td-id').tooltip('hide');		  
		});		
		
		$('#button-modal-confirm-ok-1').click(function() {
			var eraseHref = $('#button-erase').attr('href');
			console.log('#modal-confirm-ok click: ' + eraseHref);
			document.location.href = eraseHref;							
		});
		
		$('#button-modal-confirm-ok-2').click(function() {
			window.location = '${urlOrders}/actualization-postpay';	
		});
	</script>  	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


