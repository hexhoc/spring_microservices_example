package com.optimagrowth.license.controller;

import static java.util.Collections.singletonMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.optimagrowth.license.model.utils.ErrorMessage;
import com.optimagrowth.license.model.utils.ResponseWrapper;
import com.optimagrowth.license.model.utils.RestErrorList;

/**
 *
 * @author Vladislav Zhuravskiy
 * @version
 * @since 2021-12-21
 */

// You can handle exceptions globally and centrally using classes annotated with @ControllerAdvice. We are intercepted all exception here
@ControllerAdvice
// EnableWebMvc disable spring boot autoconfiguration, and class work like Spring MVC
//@EnableWebMvc
public class ExceptionController extends ResponseEntityExceptionHandler {

    /**
     * handleException - Handles all the Exception receiving a request, responseWrapper.
     *@param request
     *@param responseWrapper
     *@return ResponseEntity<ResponseWrapper>
     * @user Vladislav Zhuravskiy
     * @since 2021-12-21
     */
    @ExceptionHandler(value = { Exception.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleException(HttpServletRequest request,
                                                                         ResponseWrapper responseWrapper){

        return ResponseEntity.ok(responseWrapper);
    }

    /**
     * handleIOException - Handles all the Authentication Exceptions of the application.
     *@param request
     *@param exception
     *@return ResponseEntity<ResponseWrapper>
     * @user Vladislav Zhuravskiy
     * @since 2021-12-21
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper> handleIOException(HttpServletRequest request, RuntimeException e){

        RestErrorList errorList = new RestErrorList(HttpStatus.NOT_ACCEPTABLE, new ErrorMessage(e.getMessage(), e.getMessage()));
        ResponseWrapper responseWrapper = new ResponseWrapper(null, singletonMap("status", HttpStatus.NOT_ACCEPTABLE), errorList);


        return ResponseEntity.ok(responseWrapper);
    }

}