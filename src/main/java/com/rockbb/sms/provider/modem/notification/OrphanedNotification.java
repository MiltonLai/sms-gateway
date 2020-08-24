package com.rockbb.sms.provider.modem.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.TimeoutException;

import java.io.IOException;

public class OrphanedNotification implements IOrphanedMessageNotification {
	private static final Logger logger = LoggerFactory.getLogger(OrphanedNotification.class);

	@Override
	public boolean process(AGateway gateway, InboundMessage msg) {
		logger.info("Orphaned handler called from Gateway: {}", gateway.getGatewayId());
		logger.info("messageType: {}", msg.getType());
		logger.info("message: {}", msg);
		//存入数据库s
		/*ShortMsg shortMsg = new ShortMsg();
		shortMsg.setSmId(msg.getUuid());
		shortMsg.setSendTime(msg.getDate());
		shortMsg.setSmsc(msg.getSmscNumber());
		shortMsg.setOriginator(msg.getOriginator());
		shortMsg.setContent(msg.getText());
		shortMsg.setRedirect(0);
		shortMsg.setLongError(0);
		shortMsg.setCreateTime(new Date());
		boolean insertSuc = true;
		try{
			getSmsReceiveDao().insertSmsReceive(shortMsg);
		} catch (Throwable e){
			insertSuc = false;
			log.error("新短信未能写入数据库", e);
		}*/
		try {
			if (gateway.deleteMessage(msg)) {
				logger.info("Deleted message: {}", msg.getId());
			} else {
				logger.error("Failed to delete message: {}", msg.getId());
			}
		} catch (TimeoutException|GatewayException|IOException|InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

}
