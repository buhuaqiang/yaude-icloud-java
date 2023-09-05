package com.yaude.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import com.yaude.common.util.DateUtils;
import org.quartz.*;

/**
 * @Description: 同步定時任務測試
 *
 * 此處的同步是指 當定時任務的執行時間大于任務的時間間隔時
 * 會等待第一個任務執行完成才會走第二個任務
 *
 *
 * @author: taoyan
 * @date: 2020年06月19日
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class AsyncJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步任務調度開始 --- ");
        try {
            //此處模擬任務執行時間 5秒  任務表達式配置為每秒執行一次：0/1 * * * * ? *
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //測試發現 每5秒執行一次
        log.info(" --- 執行完畢，時間："+DateUtils.now()+"---");
    }

}
