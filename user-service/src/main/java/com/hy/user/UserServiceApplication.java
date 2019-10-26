package com.hy.user;

import com.hy.user.annotations.GrpcService;
import com.hy.user.service.GrpcLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

@SpringBootApplication
public class UserServiceApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext configurableApplicationContext =
        SpringApplication.run(UserServiceApplication.class, args);

    Map<String, Object> grpcServiceBeanMap =
        configurableApplicationContext.getBeansWithAnnotation(GrpcService.class);
    GrpcLauncher grpcLauncher =
        configurableApplicationContext.getBean("grpcLauncher", GrpcLauncher.class);
    grpcLauncher.grpcStart(grpcServiceBeanMap);
  }

}
