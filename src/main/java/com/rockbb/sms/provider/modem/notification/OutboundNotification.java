package com.rockbb.sms.provider.modem.notification;

import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;

public class OutboundNotification implements IOutboundMessageNotification {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OrphanedNotification.class);
	public void process(AGateway gatewayId, OutboundMessage msg) {
		logger.info("Outbound notification from: {}", gatewayId.getGatewayId());
		logger.info("message: {}", msg);
	}
}
