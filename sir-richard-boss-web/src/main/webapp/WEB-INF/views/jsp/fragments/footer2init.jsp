    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <!--  
    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script>window.jQuery || document.write('<script src="${urlResources}assets/js/vendor/jquery-3.3.1.min.js"><\/script>')</script>
    -->
    <script src="${urlResources}assets/js/vendor/jquery-3.3.1.min.js" ></script>    
    <script src="${urlResources}assets/js/vendor/popper.min.js"></script>
    <script src="${urlResources}js/bootstrap.min.js"></script>
    
    <script src="${urlResources}js/bootstrap-datepicker.min.js"></script>
	<script src="${urlResources}locales/bootstrap-datepicker.ru.min.js"></script>
	<script src="${urlResources}js/datatables.js"></script>
	
	<script src="${urlResources}js/jquery.mask.min.js"></script>
	<script src="${urlResources}js/hello.js"></script>

    <!-- CDEK -->    
    <script type="text/javascript" src="https://www.cdek.ru/website/edostavka/template/js/widjet.js" id="ISDEKscript" ></script>
    
    <script>     
      //feather.replace();
	  String.prototype.replaceAll = function(search, replace) {
		  return this.split(search).join(replace);
	  }
	  String.prototype.toNumber = function() {
		  return Number(this.replace(',00', '').replace(',', '.').replace(new RegExp('[^0-9,\.]'), ''));
		  
	  }	  
	  String.prototype.toFloat = function() {
		  return parseFloat(this.replace(',00', '').replace(',', '.'));
	  }
	  String.prototype.cleanTrashString = function() {
		    var output = "";
		    for (var i = 0; i < this.length; i++) {
		        if (this.charCodeAt(i) <= 127) {
		            output += this.charAt(i);
		        }
		    }
		    return output;
	  }
	  
	  Number.prototype.formatMoney = function(c, d, t) {
		    var n = this, 
		    c = isNaN(c = Math.abs(c)) ? 2 : c, 
		    d = d == undefined ? "." : d, 
		    t = t == undefined ? "," : t, 
		    s = n < 0 ? "-" : "", 
		    i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))), 
		    j = (j = i.length) > 3 ? j % 3 : 0;
		    
		    if (n > 100000) {
		    	return n;
		    }		    
		   return s + (j ? i.substr(0, j) + t : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, ' 1' + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : '');
	  }
	  
	  function searchOrderByConditions() {
		  var searchOrderNo = $('#input-search-by-order-conditions').val();
		  var searchHref = '${urlOrders}/show/by-conditions/' + searchOrderNo;
		  document.location.href = searchHref;	
	  }
	  
      $(function () {
			$('[data-toggle="tooltip"]').tooltip();			
			$('[data-toggle="popover"]').popover();		
			
			$('.input-mask-time').mask('00:00');
			//$('.input-mask-phone').mask('(000) 000-00-00');
			$(".datepicker").datepicker({
				format: "dd.mm.yyyy",
			    weekStart: 1,
			    todayBtn: "linked",
			    language: "ru",
			    orientation: "auto",
			    autoclose: true,
			    todayHighlight: true
			});
		});
      $(".nav-menus-item").removeClass('active');
  	  $('.needs-validation').attr('novalidate', '');
	  $('.form-control-required').attr('required', '');
	  	  
	  
	  $('.search-by-order-no').on('keypress',function(e) {
		  if(e.which == 13) {
			  searchOrderByConditions();
		  }
		});
	  
	  $('#link-search-by-order-no').click(function() {
		  searchOrderByConditions();
	  });
	  

    </script>    
