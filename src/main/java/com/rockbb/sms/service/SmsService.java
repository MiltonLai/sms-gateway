package com.rockbb.sms.service;

import com.rockbb.sms.bo.SmsMessage;
import com.rockbb.sms.provider.modem.ModemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Repository("smsService")
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private ModemService modemService;

    @PostConstruct
    public void init() {
        logger.info("PostConstruct");
        modemService = new ModemService();
        modemService.init();
    }

    @PreDestroy
    public void close() {
        logger.info("PreDestroy");
        modemService.stop();
    }

    public void stop() {
        modemService.stop();
    }

    public boolean send(SmsMessage sms) {
        if (!modemService.isReady()) {
            return false;
        }
        OutboundMessage outboundMessage = new OutboundMessage(sms.getRecipient(), sms.getContent());
        return modemService.send(outboundMessage);
    }

    public int countInboundMessages() {
        return modemService.countInboundMessages();
    }

    public List<InboundMessage> listInboundMessages(int offset, int limit) {
        return modemService.listInboundMessages(offset, limit);
    }

    public int countOutboundMessages() {
        return modemService.countOutboundMessages();
    }

    public List<OutboundMessage> listOutboundMessages(int offset, int limit) {
        return modemService.listOutboundMessages(offset, limit);
    }

    public void purge() {
        if (!modemService.isReady()) {
            return;
        }
        modemService.purge();
    }
}
