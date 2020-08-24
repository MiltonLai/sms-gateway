package com.rockbb.sms.web.interceptor;

import com.rockbb.sms.commons.InetAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class GlobalInterceptor extends HandlerInterceptorAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(GlobalInterceptor.class);

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) throws Exception {
        logger.debug("Request end, {}", System.currentTimeMillis());
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView mav) throws Exception {
        if (handler instanceof HandlerMethod && mav != null) {

        }
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            String ip = InetAddressUtil.getAddressFromRequest(request);
            logger.debug("Request start: {}, {}, User-Agent: {}", System.currentTimeMillis(), ip, request.getHeader("User-Agent"));
            return true;
        }
        logger.debug("Request pass: {}, {}, User-Agent: {}", System.currentTimeMillis(), request.getRemoteAddr(), request.getHeader("User-Agent"));
        return true;
    }
}
