package com.rockbb.sms.provider.modem;

import com.rockbb.sms.provider.modem.bean.CommPortDevice;
import com.rockbb.sms.provider.modem.notification.CallNotification;
import com.rockbb.sms.provider.modem.notification.GatewayStatusNotification;
import com.rockbb.sms.provider.modem.notification.InboundNotification;
import com.rockbb.sms.provider.modem.notification.OrphanedNotification;
import com.rockbb.sms.provider.modem.notification.OutboundNotification;
import com.rockbb.sms.provider.modem.util.CommUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.InboundMessage;
import org.smslib.Message;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;
import org.smslib.queues.AbstractQueueManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ModemService {
    private static final Logger logger = LoggerFactory.getLogger(ModemService.class);
    private static final int HISTORY_SIZE = 5000;

    private final Service service = Service.getInstance();
    private final LinkedList<OutboundMessage> outboundMessages = new LinkedList<>();
    private final LinkedList<InboundMessage> inboundMessages = new LinkedList<>();
    private boolean ready = false;

    synchronized public void init() {
        service.setOutboundMessageNotification(new OutboundNotification());
        service.setInboundMessageNotification(new InboundNotification(inboundMessages));
        service.setOrphanedMessageNotification(new OrphanedNotification());
        service.setCallNotification(new CallNotification());
        service.setGatewayStatusNotification(new GatewayStatusNotification());

        List<CommPortDevice> devices = CommUtil.getCommPortDevices();
        List<SerialModemGateway> modemGateways = new ArrayList<>();
        for (CommPortDevice device : devices) {
            logger.info("Add gateway: {},{}", device.getSerialPort(), device.getBaudRate());
            SerialModemGateway gateway = new SerialModemGateway(
                    "modem." + device.getSerialPort(), device.getSerialPort(), device.getBaudRate(), "WAVECOM", "");
            gateway.setInbound(true);
            gateway.setOutbound(true);
            try {
                service.addGateway(gateway);
                modemGateways.add(gateway);
            } catch (GatewayException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // 启用轮循模式
        service.S.SERIAL_POLLING = true;
        // 关闭运营商选择
        service.S.DISABLE_COPS = true;
        try {
            logger.info("ModemService starting");
            service.startService();
            ready = true;
            logger.info("ModemService started");
        } catch (SMSLibException |IOException|InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        for (SerialModemGateway gateway : modemGateways) {
            try{
                logger.debug("Gateway: {}", gateway.getGatewayId());
                logger.debug("  Manufacturer: {}", gateway.getManufacturer());
                logger.debug("  Model: {}", gateway.getModel());
                logger.debug("  Serial No: {}", gateway.getSerialNo());
                logger.debug("  SIM IMSI: {}", gateway.getImsi());
                logger.debug("  Signal Level: {}", gateway.getSignalLevel() + " dBm");
                logger.debug("  Battery Level: {}", gateway.getBatteryLevel() + "%");
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    synchronized public void stop() {
        try {
            ready = false;
            service.stopService();
        } catch (SMSLibException|IOException|InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    synchronized public boolean send(OutboundMessage msg) {
        msg.setEncoding(Message.MessageEncodings.ENCUCS2); // 中文
        msg.setStatusReport(true); // 发送状态报告
        try {
            outboundMessages.addFirst(msg);
            logger.info("Send message: {}, {}", msg.getRefNo(), msg.getText());
            boolean result = service.sendMessage(msg);
            logger.info("Send message done");
            return result;
        } catch (TimeoutException |GatewayException|IOException|InterruptedException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    synchronized public boolean queue(OutboundMessage msg) {
        AbstractQueueManager queueManager = service.getQueueManager();
        for (AGateway gateway : service.getGateways()) {
            int queueSize = queueManager.pendingQueueSize(gateway.getGatewayId());
            logger.info("Queue size: {} {}", gateway.getGatewayId(), queueSize);
        }
        msg.setEncoding(Message.MessageEncodings.ENCUCS2); // 中文
        msg.setStatusReport(true); // 发送状态报告
        return service.queueMessage(msg);
    }

    public int countInboundMessages() {
        return inboundMessages.size();
    }

    public List<InboundMessage> listInboundMessages(int offset, int limit) {
        if (offset > inboundMessages.size() - 1)
            offset = inboundMessages.size() - 1;
        if (offset < 0)
            offset = 0;

        int to = offset + limit;
        if (to < offset)
            to = offset;
        if (to > inboundMessages.size())
            to = inboundMessages.size();

        return inboundMessages.subList(offset, to);
    }

    public boolean isReady() {
        return ready;
    }

    public int countOutboundMessages() {
        return outboundMessages.size();
    }

    public List<OutboundMessage> listOutboundMessages(int offset, int limit) {
        if (offset > outboundMessages.size() - 1)
            offset = outboundMessages.size() - 1;
        if (offset < 0)
            offset = 0;

        int to = offset + limit;
        if (to < offset)
            to = offset;
        if (to > outboundMessages.size())
            to = outboundMessages.size();

        return outboundMessages.subList(offset, to);
    }

    synchronized public void purge() {
        while (inboundMessages.size() > HISTORY_SIZE) {
            inboundMessages.removeLast();
        }
        while (outboundMessages.size() > HISTORY_SIZE) {
            outboundMessages.removeLast();
        }
    }

    public static void main(String[] args) {
        ModemService modemService = new ModemService();
        modemService.init();
        /*OutboundMessage msg = new OutboundMessage("13810291102", "English 中文短信内容, 中文短信内容，中文短信内容");
        boolean result = modemService.send(msg);
        logger.info("result {}", result);*/
        logger.info("Now Sleeping - Hit <enter> to terminate.");
        try {
            System.in.read();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Now stopping");
        modemService.stop();
    }
}
