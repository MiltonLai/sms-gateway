package com.rockbb.sms.provider.modem.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.ICallNotification;

public class CallNotification implements ICallNotification {
    private static final Logger logger = LoggerFactory.getLogger(CallNotification.class);

    @Override
    public void process(AGateway aGateway, String s) {
        logger.info("Call from {} by {}", aGateway.getGatewayId(), s);
    }

}
