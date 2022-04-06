package ru.sir.richard.boss.model.utils;

import org.junit.Test;
import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlUtilsTest {
	
	public static final String xmlFilePath = "c:\\src\\sir-richard-boss\\--save\\yandex-market.xml";
		
	//@Test
	public void stringToXml_v1() {
		/*
		<?xml version="1.0" encoding="UTF-8" standalone="no"?>
		<company>
			<employee id="10">
				<firstname>James</firstname>
				<lastname>Harley</lastname>
				<email>james@example.org</email>
				<department>Human Resources</department>
			</employee>
		</company>
		*/
		
		try {
			 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("company");
            document.appendChild(root);
 
            // employee element
            Element employee = document.createElement("employee");
 
            root.appendChild(employee);
 
            // set an attribute to staff element
            Attr attr = document.createAttribute("id");
            attr.setValue("10");
            employee.setAttributeNode(attr);
 
            //you can also use staff.setAttribute("id", "1") for this
 
            // firstname element
            Element firstName = document.createElement("firstname");
            firstName.appendChild(document.createTextNode("James"));
            employee.appendChild(firstName);
 
            // lastname element
            Element lastname = document.createElement("lastname");
            lastname.appendChild(document.createTextNode("Harley"));
            employee.appendChild(lastname);
 
            // email element
            Element email = document.createElement("email");
            email.appendChild(document.createTextNode("james@example.org"));
            employee.appendChild(email);
 
            // department elements
            Element department = document.createElement("department");
            department.appendChild(document.createTextNode("Human Resources"));
            employee.appendChild(department);
 
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
 
            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging 
 
            transformer.transform(domSource, streamResult);
 
            System.out.println("Done creating XML File");
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
		
		
		
	}
	
	@Test
	public void stringToXml_v2() {
		
		/*
<!DOCTYPE yml_catalog SYSTEM "shops.dtd">
<yml_catalog date="2010-04-01 17:00">
    <shop>
        <name>Magazin</name>
        <company>Magazin</company>
        <url>http://www.magazin.ru/</url>

        <currencies>
            <currency id="RUR" rate="1" plus="0"/>
        </currencies>
        
        <delivery-options>
          <option cost="300" days="1-3"/>
        </delivery-options>
        
        <categories>
            <category id="1">Оргтехника</category>
            <category id="10" parentId="1">Принтеры</category>
            <category id="100" parentId="10">Струйные принтеры</category>
            <category id="101" parentId="10">Лазерные принтеры</category>

            <category id="2">Фототехника</category>
            <category id="11" parentId="2">Фотоаппараты</category>
            <category id="12" parentId="2">Объективы</category>

            <category id="3">Книги</category>
            <category id="13" parentId="3">Детективы</category>
            <category id="14" parentId="3">Художественная литература</category>
            <category id="15" parentId="3">Учебная литература</category>
            <category id="16" parentId="3">Детская литература</category>

            <category id="4">Музыка и видеофильмы</category>
            <category id="17" parentId="4">Музыка</category>
            <category id="18" parentId="4">Видеофильмы</category>

            <category id="5">Путешествия</category>
            <category id="19" parentId="5">Туры</category>
            <category id="20" parentId="5">Авиабилеты</category>

            <category id="6">Билеты на мероприятия</category>
        </categories>

		<offers>
            <offer id="12341" type="vendor.model" bid="13" cbid="20" available="true">
                <url>http://magazin.ru/product_page.asp?pid=14344</url>
                <price>15000</price>
                <currencyId>RUR</currencyId>
                <categoryId type="Own">101</categoryId>
                <picture>http://magazin.ru/img/device14344.jpg</picture>
                <delivery>true</delivery>
                <local_delivery_cost>300</local_delivery_cost>
                <typePrefix>Принтер</typePrefix>
                <vendor>НP</vendor>
                <vendorCode>Q7533A</vendorCode>
                <model>Color LaserJet 3000</model>
                <description>A4, 64Mb, 600x600 dpi, USB 2.0, 29стр/мин ч/б / 15стр/мин цв, лотки на 100л и 250л, плотность до 175г/м, до 60000 стр/месяц </description>
                <manufacturer_warranty>true</manufacturer_warranty>
                <country_of_origin>Япония</country_of_origin>
            </offer>

		    		    
		*/
				
		try {
			 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("yml_catalog");
            document.appendChild(root);
            
            Attr attrRootDate = document.createAttribute("date");
            attrRootDate.setValue(DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
            root.setAttributeNode(attrRootDate);
 
            // employee element
            Element shop = document.createElement("shop"); 
            root.appendChild(shop);
            
            Element shopName = document.createElement("name");
            shopName.appendChild(document.createTextNode("СЭР РИЧАРД РУ"));            
            shop.appendChild(shopName);
            
            Element shopCompany = document.createElement("company");
            shopCompany.appendChild(document.createTextNode("ИП ФЕДОРОВ А.А."));            
            shop.appendChild(shopCompany);
            
            Element shopUrl = document.createElement("url");
            shopUrl.appendChild(document.createTextNode("https://sir-richard.ru"));            
            shop.appendChild(shopUrl);
            
            Element currencies = document.createElement("currencies");
            shop.appendChild(currencies);
            
            Element currencyRur = document.createElement("currency");            
            Attr attrCurrencyRurId = document.createAttribute("id");
            attrCurrencyRurId.setValue("RUR");
            currencyRur.setAttributeNode(attrCurrencyRurId);
            Attr attrCurrencyRurRate = document.createAttribute("rate");
            attrCurrencyRurRate.setValue("1");
            currencyRur.setAttributeNode(attrCurrencyRurRate);
            Attr attrCurrencyRurPlus = document.createAttribute("plus");
            attrCurrencyRurPlus.setValue("0");
            currencyRur.setAttributeNode(attrCurrencyRurPlus); 
            currencies.appendChild(currencyRur);     
                        
            Element deliveryOptions = document.createElement("delivery-options");
            shop.appendChild(deliveryOptions);
          
            Element deliveryOption = document.createElement("option");            
            Attr deliveryOptionCost = document.createAttribute("cost");
            deliveryOptionCost.setValue("300");
            deliveryOption.setAttributeNode(deliveryOptionCost);          
            Attr deliveryOptionDays = document.createAttribute("days");
            deliveryOptionDays.setValue("1-2");
            deliveryOption.setAttributeNode(deliveryOptionDays);
            deliveryOptions.appendChild(deliveryOption);     
            
            Element categories = document.createElement("categories");
            shop.appendChild(categories);
            
            Element categoryElement = document.createElement("category");            
            Attr attrCategoryElementId = document.createAttribute("id");
            attrCategoryElementId.setValue("1");
            categoryElement.setAttributeNode(attrCategoryElementId);            
            categoryElement.appendChild(document.createTextNode("Отпугиватели птиц"));
            categories.appendChild(categoryElement);
            
            Element offers = document.createElement("offers");
            shop.appendChild(offers);
                        
            Element offerElement = document.createElement("offer");            
            Attr attrOfferElementId = document.createAttribute("id");
            attrOfferElementId.setValue("12341");
            offerElement.setAttributeNode(attrOfferElementId); 
            Attr attrOfferElementType = document.createAttribute("type");
            attrOfferElementType.setValue("vendor.model");
            offerElement.setAttributeNode(attrOfferElementType);
            Attr attrOfferElementAvalable = document.createAttribute("available");
            attrOfferElementAvalable.setValue("true");
            offerElement.setAttributeNode(attrOfferElementAvalable);
            
            Element offerElementUrl = document.createElement("url"); 
            offerElementUrl.appendChild(document.createTextNode("https://sir-richard.ru/borba-s-vreditelyami/otpugivateli-ptic-ultrazvukovye/propanovye-grompushki/zon-el08-teleskop"));
            offerElement.appendChild(offerElementUrl);
            
            Element offerElementPrice = document.createElement("price"); 
            offerElementPrice.appendChild(document.createTextNode("32990"));
            offerElement.appendChild(offerElementPrice);
            
            Element offerElementCurreny = document.createElement("currencyId"); 
            offerElementCurreny.appendChild(document.createTextNode("RUR"));
            offerElement.appendChild(offerElementCurreny);
            
            Element offerElementCategory = document.createElement("categoryId"); 
            offerElementCategory.appendChild(document.createTextNode("1"));
            offerElement.appendChild(offerElementCategory);
            // TODO type=Own
            
            Element offerElementPicture = document.createElement("picture"); 
            offerElementPicture.appendChild(document.createTextNode("https://sir-richard.ru/image/cache/data/repeller/bird/grompushka/grompushka-zon-el08-teleskop/Zon-EL08-teleskop_1-600x600.jpg"));
            offerElement.appendChild(offerElementPicture);
            
            Element offerElementDelivery = document.createElement("delivery"); 
            offerElementDelivery.appendChild(document.createTextNode("true"));
            offerElement.appendChild(offerElementDelivery);
            
            Element offerElementTypePreffix = document.createElement("typePrefix"); 
            offerElementTypePreffix.appendChild(document.createTextNode("пропановый отпугиватель"));
            offerElement.appendChild(offerElementTypePreffix);
            
            Element offerElementTypeVendor = document.createElement("vendor"); 
            offerElementTypeVendor.appendChild(document.createTextNode("DAZON"));
            offerElement.appendChild(offerElementTypeVendor);
            
            Element offerElementModel = document.createElement("model"); 
            offerElementModel.appendChild(document.createTextNode("Электронный отпугиватель птиц громпушка \"Zon EL08\" с телескопическим дулом"));
            offerElement.appendChild(offerElementModel);
                        
            Element offerElementDescription = document.createElement("description"); 
            offerElementDescription.appendChild(document.createTextNode("Громпушка \"Zon EL08\" оснащена электронным управлением и телескопическим стволом, позволяющим регулировать мощность выстрела в пределах 100...130 дБ. Эффективно прогоняет пернатых \"разбойников\" на водохранилищах, аэродромах, полях и садах площадью до 2 гектаров. Отпугивает одиночными или сериями из 2, 3, 4, 5 выстрелов, снабжена 4 таймерами для программирования работы по расписанию. Паузы между сериями \"выстрелов\" можно настаивать в диапазоне 1...60 минут или включить случайный выбор. Использует сжиженный пропан, расходуется 1-3 литра в месяц (в зависимости от настроек), питание от аккумулятора 12В. Разработана и произведена в Голландии."));
            offerElement.appendChild(offerElementDescription);
            
            //В элементе name указывается заголовок товарного предложения (длина не более 120 символов). В заголовке рекомендуется указывать полное уникальное название товара. Заголовок не может состоять только из цифр.
            //В элементе description указывается описание товарного предложения (длина не более 175 символов). В описании товарного предложения рекомендуется указывать основные характеристики товара.
              
            
            offers.appendChild(offerElement);
                    
            /*
            <offers>
            <offer id="12341" type="vendor.model" bid="13" cbid="20" available="true">
                <url>http://magazin.ru/product_page.asp?pid=14344</url>
                <price>15000</price>
                <currencyId>RUR</currencyId>
                <categoryId type="Own">101</categoryId>
                <picture>http://magazin.ru/img/device14344.jpg</picture>
                <delivery>true</delivery>

                <typePrefix>Принтер</typePrefix>
                <vendor>НP</vendor>
                <vendorCode>Q7533A</vendorCode>
                <model>Color LaserJet 3000</model>
                <description>A4, 64Mb, 600x600 dpi, USB 2.0, 29стр/мин ч/б / 15стр/мин цв, лотки на 100л и 250л, плотность до 175г/м, до 60000 стр/месяц </description>
                <manufacturer_warranty>true</manufacturer_warranty>
                <country_of_origin>Япония</country_of_origin>
            </offer>
            */
            
            
            
                                  
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
 
            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging
            
            
            document.setXmlVersion("1.0");
            //transformer.setOutputProperty(OutputKeys.ENCODING, "windows-1251");
            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yml_catalog SYSTEM");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "shops.dtd");

            transformer.transform(domSource, streamResult);
 
            System.out.println("Done creating XML File");
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
		
	}

}
