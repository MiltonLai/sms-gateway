package com.rockbb.sms.provider.modem.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.IGatewayStatusNotification;

public class GatewayStatusNotification implements IGatewayStatusNotification {
    private static final Logger logger = LoggerFactory.getLogger(GatewayStatusNotification.class);

    @Override
    public void process(AGateway aGateway, AGateway.GatewayStatuses gatewayStatuses, AGateway.GatewayStatuses gatewayStatuses1) {
        logger.info("Gateway status notification from {}: {}, {}", aGateway.getGatewayId(), gatewayStatuses, gatewayStatuses1);
    }
}