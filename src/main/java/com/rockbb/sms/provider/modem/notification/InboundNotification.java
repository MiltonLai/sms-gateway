package com.rockbb.sms.provider.modem.notification;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.TimeoutException;
import org.smslib.Message.MessageTypes;


public class InboundNotification implements IInboundMessageNotification {
    private static final Logger logger = LoggerFactory.getLogger(InboundNotification.class);
    private final List<InboundMessage> inboundMessages;

    public InboundNotification(List<InboundMessage> inboundMessages) {
        this.inboundMessages = inboundMessages;
    }

    @Override
    public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
        logger.info("Inbound notification from: {}", gateway.getGatewayId());
        logger.info("messageType: {}", msgType);
        logger.info("message: {}", msg);
        inboundMessages.add(0, msg);

        if (msg.getMemLocation().equals("SR")) { //状态报告，直接删除
            try {
                if (gateway.deleteMessage(msg)) {
                    logger.info("Deleted status report: {}", msg.getId());
                } else {
                    logger.error("Failed to delete status report: {}", msg.getId());
                }
            } catch (TimeoutException|GatewayException|IOException|InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            //存入数据库
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
				logger.error("新短信未能写入数据库", e);
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
        }
    }

}
