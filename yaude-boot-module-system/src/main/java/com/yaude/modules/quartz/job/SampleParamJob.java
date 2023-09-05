package com.yaude.modules.quartz.job;

import com.yaude.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

/**
 * 示例帶參定時任務
 * 
 * @Author Scott
 */
@Slf4j
public class SampleParamJob implements Job {

	/**
	 * 若參數變量名修改 QuartzJobController中也需對應修改
	 */
	private String parameter;

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" Job Execution key："+jobExecutionContext.getJobDetail().getKey());
		log.info( String.format("welcome %s! Jeecg-Boot 帶參數定時任務 SampleParamJob !   時間:" + DateUtils.now(), this.parameter));
	}
}
