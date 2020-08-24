package com.rockbb.sms.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rockbb.sms.bo.SmsMessage;
import com.rockbb.sms.commons.JacksonUtils;
import com.rockbb.sms.commons.SecureUtil;
import com.rockbb.sms.service.SmsService;
import com.rockbb.sms.vo.SmsMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
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
import java.util.ArrayList;
import java.util.List;
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

    @RequestMapping(value = "/inbound_list")
    @ResponseBody
    public String doInboundList(HttpServletRequest request, HttpServletResponse response) {
        String strOffset = request.getParameter("offset");
        String strLimit = request.getParameter("limit");
        int offset = (strOffset == null)? 0 : Integer.parseInt(strOffset);
        int limit = (strLimit == null)? 20 : Integer.parseInt(strLimit);
        List<InboundMessage> messages = smsService.listInboundMessages(offset, limit);
        List<SmsMessageVO> vos = adapt(messages);
        return JacksonUtils.compressList(vos);
    }

    @RequestMapping(value = "/outbound_list")
    @ResponseBody
    public String doOutboundList(HttpServletRequest request, HttpServletResponse response) {
        String strOffset = request.getParameter("offset");
        String strLimit = request.getParameter("limit");
        int offset = (strOffset == null)? 0 : Integer.parseInt(strOffset);
        int limit = (strLimit == null)? 20 : Integer.parseInt(strLimit);
        List<OutboundMessage> messages = smsService.listOutboundMessages(offset, limit);
        List<SmsMessageVO> vos = adapt2(messages);
        return JacksonUtils.compressList(vos);
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

    private static SmsMessageVO adapt(InboundMessage msg) {
        if (msg == null) return null;
        SmsMessageVO vo = new SmsMessageVO();
        vo.setSender(msg.getOriginator());
        vo.setRecipient(msg.getGatewayId());
        if (msg.getMemLocation().equals("SR")) {
            vo.setContent("Delivered Report");
        } else {
            vo.setContent(msg.getText());
        }
        vo.setDate(msg.getDate());
        return vo;
    }

    private static List<SmsMessageVO> adapt(List<InboundMessage> msgs) {
        if (msgs == null) return null;
        List<SmsMessageVO> vos = new ArrayList<>();
        for (InboundMessage msg : msgs) {
            SmsMessageVO vo = adapt(msg);
            if (vo != null) {
                vos.add(vo);
            }
        }
        return vos;
    }

    private static SmsMessageVO adapt2(OutboundMessage msg) {
        if (msg == null) return null;
        SmsMessageVO vo = new SmsMessageVO();
        vo.setSender(msg.getGatewayId());
        vo.setRecipient(msg.getRecipient());
        vo.setContent(msg.getText());
        vo.setDate(msg.getDate());
        vo.setStatus(msg.getMessageStatus().name());
        vo.setNote(msg.getFailureCause().name());
        return vo;
    }

    private static List<SmsMessageVO> adapt2(List<OutboundMessage> msgs) {
        if (msgs == null) return null;
        List<SmsMessageVO> vos = new ArrayList<>();
        for (OutboundMessage msg : msgs) {
            SmsMessageVO vo = adapt2(msg);
            if (vo != null) {
                vos.add(vo);
            }
        }
        return vos;
    }

}
