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
import org.springframework.web.servlet.ModelAndView;

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
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Resource(name="smsService")
    private SmsService smsService;

    @RequestMapping(value = "/inbound_list")
    public ModelAndView doInboundList(HttpServletRequest request) {
        String strOffset = request.getParameter("offset");
        String strLimit = request.getParameter("limit");
        int offset = (strOffset == null)? 0 : Integer.parseInt(strOffset);
        int limit = (strLimit == null)? 20 : Integer.parseInt(strLimit);
        List<InboundMessage> messages = smsService.listInboundMessages(offset, limit);
        List<SmsMessageVO> vos = adapt(messages);
        ModelAndView mav = new ModelAndView("inbound_list");
        mav.addObject("vos", vos);
        return mav;
    }

    @RequestMapping(value = "/outbound_list")
    public ModelAndView doOutboundList(HttpServletRequest request) {
        String strOffset = request.getParameter("offset");
        String strLimit = request.getParameter("limit");
        int offset = (strOffset == null)? 0 : Integer.parseInt(strOffset);
        int limit = (strLimit == null)? 20 : Integer.parseInt(strLimit);
        List<OutboundMessage> messages = smsService.listOutboundMessages(offset, limit);
        List<SmsMessageVO> vos = adapt2(messages);
        ModelAndView mav = new ModelAndView("outbound_list");
        mav.addObject("vos", vos);
        return mav;
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
