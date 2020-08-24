package com.rockbb.sms.provider.modem.util;

import com.rockbb.sms.provider.modem.bean.CommPortDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.helper.CommPortIdentifier;
import org.smslib.helper.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class CommUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommUtil.class);
    private static final int[] baudRates = {9600}; //, 19200, 57600, 115200

    public static List<CommPortDevice> getCommPortDevices() {
        List<CommPortDevice> devices = new ArrayList<>();
        logger.info("Detecting serial comm　devices...");
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                logger.info("Found serial comm device: {}", portId.getName());
                for (int baudRate : baudRates) {
                    logger.info("  Trying at {} ...", baudRate);
                    SerialPort serialPort = null;
                    try {
                        serialPort = portId.open("SMSLibCommTester", 1971);
                        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                        serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        InputStream inStream = serialPort.getInputStream();
                        OutputStream outStream = serialPort.getOutputStream();
                        serialPort.enableReceiveTimeout(1000);
                        int c;
                        while ((c = inStream.read()) != -1) {
                            // do nothing;
                        }
                        outStream.write("AT\r".getBytes());
                        StringBuilder sb = new StringBuilder();
                        while ((c = inStream.read()) != -1) {
                            sb.append((char)c);
                        }
                        if (!sb.toString().contains("OK")) {
                            logger.info("    No device detected, response: {}", sb);
                        } else {
                            logger.info("    Device found,");
                            sb.setLength(0); // clear the StringBuilder
                            outStream.write("AT+CGMM\r".getBytes());
                            while ((c = inStream.read()) != -1) {
                                sb.append((char)c);
                            }
                            String model = sb.toString()
                                    .replaceAll("(\n|\r|AT\\+CGMM|OK)", "")
                                    .replaceAll("\\s+", " ").trim();
                            logger.info("    Device model: {}", model);
                            CommPortDevice device = new CommPortDevice();
                            device.setSerialPort(portId.getName());
                            device.setBaudRate(baudRate);
                            device.setModel(model);
                            devices.add(device);
                            break;
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        if (serialPort != null) {
                            serialPort.close();
                        }
                    }
                }
            }
        }
        return devices;
    }

    public static void main(String[] args) {
        List<CommPortDevice> devices = getCommPortDevices();
        logger.info("size: {}", devices.size());
    }
}
