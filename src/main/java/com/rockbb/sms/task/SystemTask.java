package com.rockbb.sms.task;

import com.rockbb.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("systemTask")
@EnableScheduling
public class SystemTask {
    private static final Logger logger = LoggerFactory.getLogger(SystemTask.class);

    @Resource(name="smsService")
    private SmsService smsService;

    /**
     * 清理
     */
    @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes
    public void doSmsHistoryPurge() {
        logger.debug("Sms history cleanup: start");
        smsService.purge();
        logger.debug("Sms history cleanup: end");
    }
}
