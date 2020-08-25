package com.rockbb.sms.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rockbb.sms.bo.SmsMessage;
import com.rockbb.sms.commons.JacksonUtils;
import com.rockbb.sms.commons.SecureUtil;
import com.rockbb.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Controller
public class IndexApi {
    private static final Logger logger = LoggerFactory.getLogger(IndexApi.class);
    private static final String AES_KEY = "f0ad15acf6cd0001";

    @Autowired
    MessageSource messageSource;

    @Resource(name="smsService")
    private SmsService smsService;

    @RequestMapping(value = "/api")
    @ResponseBody
    public String doApi(HttpServletRequest request, HttpServletResponse response) {
        String merId = request.getParameter("merId");
        String ts = request.getParameter("ts");
        try {
            String encrypted = inputStream2String(request.getInputStream());
            if (encrypted == null || encrypted.length() == 0) return "error: empty input";
            String json = SecureUtil.aesDecrypt(encrypted, AES_KEY);
            logger.debug("<<: {}", json);
            if (json == null || json.length() == 0) return "error: invalid input";
            SmsMessage message = JacksonUtils.extractByTypeRef(json, new TypeReference<SmsMessage>() {});
            if (message == null) {
                return "error: invalid message";
            }
            boolean result = smsService.send(message);
            if (result) {
                return "success";
            } else {
                return "error";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "error: " + e.getMessage();
        }
    }

    @RequestMapping(value = "/stop")
    @ResponseBody
    public String doStop() {
        smsService.stop();
        return "ok";
    }

    @RequestMapping(value = "/start")
    @ResponseBody
    public String doStart() {
        smsService.start();
        return "ok";
    }

    /**
     * 将输入流转读取成字符串
     */
    public static String inputStream2String(InputStream in) throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private String t(String code, String ... strings) {
        return messageSource.getMessage(code, strings, code, Locale.SIMPLIFIED_CHINESE);
    }
    private String t(String code) {
        return messageSource.getMessage(code, null, code, Locale.SIMPLIFIED_CHINESE);
    }

}
