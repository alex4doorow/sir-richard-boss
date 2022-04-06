<%@ include file = "fragments/header2init.jsp" %>
<%@ include file = "fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /></title>
    
<%@ include file = "fragments/header2html2navbar2start.jsp" %>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">   
<!-- local content -->            
<h1 class="h2"><fmt:message key="index.header" /></h1>
 
<%@ include file = "fragments/header2html2navbar2finish.jsp" %>         
</div>    
<!-- alert block ets -->
   
<%@ include file = "fragments/footer2init.jsp" %>
<!-- local java script -->
<script>      
	$('#nav-link-home').addClass('active');
	$('#nav-link-home i').removeClass('text-dark').addClass('text-info');
</script>  
<%@ include file = "fragments/footer2html.jsp" %>



  