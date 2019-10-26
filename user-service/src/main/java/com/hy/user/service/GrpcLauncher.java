package com.hy.user.service;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class GrpcLauncher {

    private Server server;

    @Value("${grpc.server.port}")
    private Integer grpcServerPort;

    public void grpcStart(Map<String, Object> grpcServiceBeanMap) {
        try {
            ServerBuilder serverBuilder = ServerBuilder.forPort(grpcServerPort);
            for (Object bean : grpcServiceBeanMap.values()){
                serverBuilder.addService((BindableService) bean);
                log.info(bean.getClass().getSimpleName() + " is registered in Spring Boot");
            }
            server = serverBuilder.build().start();
            log.info("grpc server is started at " +  grpcServerPort);
            server.awaitTermination();
            Runtime.getRuntime().addShutdownHook(new Thread(()->{grpcStop();}));
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    private void grpcStop() {
        if (server != null) {
            server.shutdownNow();
        }
    }

}
