package com.xwq.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Job监听
 * @author WQXia
 * @date 2017-03-31 13:35:21
 * @version 1.0
 */
public class CsvJobListener implements JobExecutionListener{

	long startTime;
	long endTime;
	
	@Override
	public void beforeJob(JobExecution arg0) {
		startTime = System.currentTimeMillis();
		System.out.println("任务开始");
	}

	@Override
	public void afterJob(JobExecution arg0) {
		endTime = System.currentTimeMillis();
		System.out.println("任务处理结束");
		System.out.println("耗时：" + (endTime-startTime) + "ms");
	}

}
