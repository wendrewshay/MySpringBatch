//package com.xwq.custom;
//
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
//import org.springframework.stereotype.Component;
//
///**
// * 定制Servlet容器，通过ApplicationArguments类可访问java命令行传入的参数
// * 示例:java -jar xx.jar --port 8081
// *      java -jar xx.jar --port 8082
// * @author WQXia
// * @date 2017-04-05 11:07:46
// * @version 1.0
// */
//@Component
//public class MyServletContainerCustomization implements EmbeddedServletContainerCustomizer{
//
//	private static int port = 8080;
//	
//	@Autowired
//	public MyServletContainerCustomization(ApplicationArguments args) {
//		Set<String> optionNames = args.getOptionNames();
//		if(optionNames.contains("port")) {
//			port = Integer.parseInt(args.getOptionValues("port").get(0));
//		}
//	}
//	
//	@Override
//	public void customize(ConfigurableEmbeddedServletContainer container) {
//		container.setDisplayName("MySpringBatch");
//		container.setPort(port);
//	}
//
//}
