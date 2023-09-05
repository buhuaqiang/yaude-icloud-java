package com.yaude.icloud.quartz.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.util.DateUtils;
import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.yaude.icloud.openstack.service.IOsResourceUsageService;
import com.yaude.icloud.openstack.utils.Openstack4jEntity;
import com.yaude.icloud.openstack.utils.Openstack4jNova;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.identity.v3.Project;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 示例带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SampleParamJob implements Job {

	/**
	 * 若参数变量名修改 QuartzJobController中也需对应修改
	 */
	private String parameter;

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Autowired
	private IOsResourceUsageService osResourceUsageService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" Job Execution key："+jobExecutionContext.getJobDetail().getKey());
		log.info( String.format("测试json参数: %s !   时间:" + DateUtils.now(), this.parameter));

		String projectId ="04987b0c4ad54494a79f0c41a7fb6c02";
		String projectName = "";
		String date ="";

		//{"projectId":"04987b0c4ad54494a79f0c41a7fb6c02","date":"2021-10-15"}
		if(StringUtils.isNotEmpty(this.parameter)){
			JSONObject item = JSONObject.parseObject(this.parameter);
			//projectId = item.getString("projectId");
			projectName = item.getString("projectName");
			date = item.getString("date");
		}

		if(StringUtils.isNotEmpty(projectId)){
			Openstack4jProject openstack4jProject = new Openstack4jProject();
			List<? extends Project> projectList = openstack4jProject.getProjectList();
			for (Project p:projectList) {
				saveOrUpdate(p.getId(),date);
			}
		}
	}


	//指定日期和項目寫入資源用量
	void saveOrUpdate(String projectId,String date){
		Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
		openstack4jEntity.setProjectId(projectId);
		Openstack4jNova openstack4jNova = null;
		try {
			openstack4jNova = new Openstack4jNova(openstack4jEntity);
		}catch (AuthenticationException e){
			log.info(e.getMessage());
		}
		if(openstack4jNova == null){
			return;
		}
		Date usageDate = new Date();
		usageDate = new Date(usageDate.getTime() -  24 * 60 * 60 * 1000);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		if(StringUtils.isNotEmpty(date)){
			usageDate = DateUtils.str2Date(date,format);
		}
		String dateTime = DateUtils.date2Str(usageDate,format);
		org.openstack4j.model.compute.SimpleTenantUsage tenantUsage = openstack4jNova.getTenantUsage(projectId, dateTime+"T00:00:00", dateTime+"T23:59:59");

		OsResourceUsage osResourceUsage = new OsResourceUsage();
		osResourceUsage.setUsageDate(DateUtils.str2Date(dateTime,format));
		LambdaQueryWrapper<OsResourceUsage> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(OsResourceUsage::getUsageDate,DateUtils.str2Date(dateTime,format));
		queryWrapper.eq(OsResourceUsage::getProjectId,projectId);
		List<OsResourceUsage> list = osResourceUsageService.list(queryWrapper);
		if(list.size()>0){
			osResourceUsage.setId(list.get(0).getId());
		}
		String totalHours = tenantUsage.getTotalHours()==null? "0":tenantUsage.getTotalHours();
		double hours =  new Double(totalHours);
		hours  = (double) Math.round(hours*100) / 100;
		osResourceUsage.setTotalHours(hours+"");
		osResourceUsage.setTotalVcpusUsage(tenantUsage.getTotalVcpusUsage()==null? new BigDecimal(0) :tenantUsage.getTotalVcpusUsage());
		osResourceUsage.setTotalLocalGbUsage(tenantUsage.getTotalLocalGbUsage() ==null? new BigDecimal(0) :tenantUsage.getTotalLocalGbUsage());
		osResourceUsage.setTotalMemoryMbUsage(tenantUsage.getTotalMemoryMbUsage() ==null? new BigDecimal(0) :tenantUsage.getTotalMemoryMbUsage());
		List<? extends SimpleTenantUsage.ServerUsage> serverUsages = tenantUsage.getServerUsages();
		int ram = 0;
		if(serverUsages!=null){
			for (SimpleTenantUsage.ServerUsage s:serverUsages) {
				ram += s.getMemoryMb();
			}
		}

		osResourceUsage.setRam(ram);
		osResourceUsage.setProjectId(projectId);
		osResourceUsageService.saveOrUpdate(osResourceUsage);
	}
}
