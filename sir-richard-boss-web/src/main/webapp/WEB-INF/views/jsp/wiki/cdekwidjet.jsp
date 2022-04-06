<%@ page language="java" contentType="text/html; charset=ISO-8859-4"
    pageEncoding="ISO-8859-4"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-4">
<title>CDEK WIDJET</title>
<script type="text/javascript" src="https://www.cdek.ru/website/edostavka/template/js/widjet.js" id="ISDEKscript" ></script>
</head>
<body>

<script>
    var widjet = new ISDEKWidjet({
        defaultCity: 'Уфа',
        cityFrom: 'Омск',
        link: 'forpvz',
        path: 'https://pribormaster.ru/catalog/view/theme/zemez808/js/cdek-pvzwidget/scripts/',
        servicepath: 'https://pribormaster.ru/catalog/controller/extension/shipping/cdek/service.php' 
    });
</script>

<div id="forpvz" style="height:600px;"></div>

</body>
</html>