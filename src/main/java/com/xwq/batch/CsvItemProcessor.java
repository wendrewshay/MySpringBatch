package com.xwq.batch;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import com.xwq.domain.Person;

/**
 * 数据处理
 * @author WQXia
 * @date 2017-03-31 12:53:52
 * @version 1.0
 */
public class CsvItemProcessor extends ValidatingItemProcessor<Person>{

	@Override
	public Person process(Person item) throws ValidationException {
		//执行此才会调用自定义校验器
		super.process(item);
		
		//对数据做简单的处理
		if(item.getNation().equals("汉族")) {
			item.setNation("01");;
		} else {
			item.setNation("02");
		}
		return item;
	}

}
