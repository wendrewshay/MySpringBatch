package com.xwq.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.xwq.domain.Person;

//@Configuration
//@EnableBatchProcessing//开启批处理支持
public class CsvBatchConfig {

	@Bean
	public ItemReader<Person> reader() {
		//1.使用FlatFileItemReader读取文件
		FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
		//2.设置csv文件的路径
		reader.setResource(new ClassPathResource("people.csv"));
		//3.对csv文件的数据和领域模型做对应映射
		reader.setLineMapper(new DefaultLineMapper<Person>(){{
			setLineTokenizer(new DelimitedLineTokenizer(){{
				setNames(new String[] {"name", "age", "nation", "address"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}});
		}});
		return reader;
	}
	
	@Bean
	public ItemProcessor<Person, Person> processor() {
		//1.使用我们队ItemProcessor的自定义实现
		CsvItemProcessor processor = new CsvItemProcessor();
		//2.指定校验器
		processor.setValidator(csvBeanValidator());
		return processor;
	}
	
	@Bean
	public ItemWriter<Person> writer(DataSource dataSource) {
		//1.用JDBC批处理的JdbcBatchItemWriter来写数据到数据库
		JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		//2.设置要执行批处理的SQL语句
		String sql = "insert into person " + "(name,age,nation,address)" 
				+ " values(:name,:age,:nation,:address)";
		writer.setSql(sql);
		writer.setDataSource(dataSource);
		return writer;
	}
	
	@Bean
	public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.setDatabaseType("mysql");
		return jobRepositoryFactoryBean.getObject();
	}
	
	@Bean
	public SimpleJobLauncher jobLauncher(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository(dataSource, transactionManager));
		return jobLauncher;
	}
	
	@Bean
	public Job importJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("importJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)//为job指定step
				.end()
				.listener(csvJobListener())//绑定监听器
				.build();
	}
	
	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, 
			ItemReader<Person> reader, ItemWriter<Person> writer, ItemProcessor<Person, Person> processor) {
		return stepBuilderFactory
				.get("step1")
				.<Person,Person>chunk(65000)//批处理每次提交65000条数据
				.reader(reader)//给step绑定reader
				.processor(processor)//给step绑定processor
				.writer(writer)//给step绑定writer
				.build();
				
	}
	
	@Bean
	public CsvJobListener csvJobListener() {
		return new CsvJobListener();
	}
	
	@Bean
	public Validator<Person> csvBeanValidator() {
		return new CsvBeanValidator<>();
	}
}
