package com.wiggin.mangersys.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.wiggin.mangersys.web.vo.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * controller 增强器 原理是使用AOP对Controller控制器进行增强（前置增强、后置增强、环绕增强）
 * 启动应用后，被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法，都会作用在
 * 被 @RequestMapping 注解的方法上。
 * 
 * @ModelAttribute：在Model上设置的值，对于所有被 @RequestMapping 注解的方法中，都可以通过
 *                                   ModelMap获取，或者通过@ModelAttribute("author")也可以获取
 * @ExceptionHandler 拦截了异常，我们可以通过该注解实现自定义异常处理。其中，@ExceptionHandler 配置的 value
 *                   指定需要拦截的异常类型，下面拦截了 Exception.class 这种异常。
 * @author sxd
 * @since 2018/4/1
 */

@ControllerAdvice(annotations = { RestController.class, Controller.class })
@Slf4j
public class CustomControllerAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     * 
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    }


    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * 
     * @param model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("author", "wiggin");
    }


    /**
     * 全局异常捕捉处理
     * 
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public GenericResponse errorHandler(Exception ex) {
        ex.printStackTrace();
        return ResponseFormat.error(ExceptionCodeEnum.SERVICE_EXCEPTION.getCode(), ExceptionCodeEnum.SERVICE_EXCEPTION.getMessage());
    }


    /**
     * 拦截捕捉自定义异常 MyException.class
     * 
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public GenericResponse myErrorHandler(BusinessException ex) {
        // ex.getException().printStackTrace();
        log.error("{}", ex);
        return ResponseFormat.error(ex.getCode(), ex.getMessage());
    }


    /**
     * 统一的校验参数返回
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public GenericResponse validExceptionHandler(Exception e) {
        BindingResult bindingResult = null;
        try {
            throw e;
        } catch (BindException eb) {
            bindingResult = eb.getBindingResult();
        } catch (MethodArgumentNotValidException em) {
            bindingResult = em.getBindingResult();
        } catch (Exception ee) {
            log.error(ee.toString());
        }
        if (bindingResult == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ExceptionCodeEnum.PARAM_VALID_ERROR.getMessage());
        if (bindingResult != null && bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            stringBuilder.append(";");
            for (FieldError fieldError : fieldErrors) {
                stringBuilder.append("field:");
                stringBuilder.append(fieldError.getField());
                stringBuilder.append(";error:");
                stringBuilder.append(fieldError.getDefaultMessage());
            }
        }
        return ResponseFormat.error(ExceptionCodeEnum.PARAM_VALID_ERROR.getCode(), stringBuilder.toString());
    }


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        final String returnTypeName = returnType.getParameterType().getName();
        log.info("supports.returnTypeName => {}", returnTypeName);
        log.info("supports.returnType => {}", returnType);
        return !"org.springframework.http.ResponseEntity".equals(returnTypeName) || StringUtils.contains(returnTypeName, "com.wiggin.mangersys");
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        final String returnTypeName = returnType.getParameterType().getName();
        log.info("beforeBodyWrite.returnTypeName => {}", returnTypeName);
        log.info("beforeBodyWrite.body => {}", body);
        log.info("beforeBodyWrite.selectedContentType => {}", selectedContentType);

        if ("void".equals(returnTypeName)) {
            return ResponseFormat.sucess();
        }

        if (!selectedContentType.includes(MediaType.APPLICATION_JSON)) {
            return body;
        }

        return ResponseFormat.sucess(body);
    }
}
