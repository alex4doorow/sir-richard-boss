package ru.sir.richard.boss.web.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class ErrorController {

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorsPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("errors/error2page");

        String errorHeader = "";
        String errorMsg = "";

        int httpErrorCode = getErrorCode(httpRequest);

        log.error("renderErrorsPage(): {}, {}", httpErrorCode, httpRequest.getRequestURL());
        httpRequest.getRequestURL();

        switch (httpErrorCode) {
            case 400: {
                errorHeader = "Http Error Code: 400. Bad Request";
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorHeader = "Http Error Code: 401. Unauthorized";
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorHeader = "Http Error Code: 404. Данные отсутствуют";
                errorMsg = "Http Error Code: 404. Данные отсутствуют";
                break;
            }
            case 405: {
                errorHeader = "Http Error Code: 405. Данные отсутствуют";
                errorMsg = "Ошибка HTTP 405 указывает на то, что веб-браузер запросил доступ к одной из ваших страниц, и ваш веб-сервер распознал запрос. Однако сервер отклонил конкретный метод HTTP, который он использует. В результате ваш веб-браузер не может получить доступ к запрошенной веб-странице.";
                break;
            }
            case 500: {
                errorHeader = "Http Error Code: 500. Ошибка сервера";
                errorMsg = "Houston, we have a problem!" + "<br/>"
                        + "Скорее всего, нет подключения к базе данных - отвалился коннекшен к базе со стороны провайдера. У них это происходит регулярно. Чтобы все заработало, нужно пойти на компьютер Леши, нажать одновременно кнопки &lt;WINDOWS&gt; + &lt;D&gt;, найти на рабочем столе ярлык \"db-connect\" и запустить заново его."
                        + "<br/>"
                        + "После этого нажать одновременно кнопки &lt;Ctrl&gt; + &lt;F5&gt; - чтобы обновить страницу после повторного подключения базы данных.";
                break;
            }
        }
        errorPage.addObject("errorHeader", errorHeader);
        errorPage.addObject("errorMsg", errorMsg);
        errorPage.addObject("httpRequest", httpRequest);
        errorPage.addObject("url", httpRequest.getRequestURL());
        return errorPage;
    }

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        int httpErrorCode = getErrorCode(httpRequest);
        log.error("renderErrorPage(): {}, {}", httpErrorCode, httpRequest.getRequestURL());
        return renderErrorsPage(httpRequest);
    }

    @RequestMapping(value = "runtimeException", method = RequestMethod.GET)
    public void testThrowException() {
        throw new RuntimeException("Throwing a null pointer exception");
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        if (httpRequest == null || httpRequest.getAttribute("javax.servlet.error.status_code") == null) {
            return 500;
        }
        return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
    }
}
