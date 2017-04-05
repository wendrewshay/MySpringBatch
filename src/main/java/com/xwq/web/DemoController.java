package com.xwq.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 手动控制批处理任务执行，在此之前需要
 * 设置spring.batch.job.enabled=false以关闭job的自动执行
 * @author WQXia
 * @date 2017-04-05 16:18:45
 * @version 1.0
 */
@RestController
public class DemoController {

	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	Job importJob;
	
	public JobParameters jobParameters;
	
	/**
	 * 访问http://localhost:8080/imp?fileName=people
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/imp")
	public String imp(String fileName) throws Exception {
		String path = fileName+".csv";
		jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("input.file.name", path)
				.toJobParameters();
		jobLauncher.run(importJob, jobParameters);
		return "ok";
	}
}
