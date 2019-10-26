package com.hy;

import com.hy.user.pb.UserInfoRequest;
import com.hy.user.pb.UserInfoResponse;
import com.hy.user.pb.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import sun.jvm.hotspot.HelloWorld;

import java.util.concurrent.TimeUnit;

@Slf4j
public class GrpcClient {

    private static final String localHost = "127.0.0.1";

    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);

    }

    public ManagedChannel getChannel(int port){
        return ManagedChannelBuilder.forAddress(localHost, port)
            .disableRetry()
            .idleTimeout(2, TimeUnit.SECONDS)
            .build();
    }

    public void getUser(int port, String username) {
        ManagedChannel channel = getChannel(port);
        UserInfoRequest userInfoRequest =
            UserInfoRequest.newBuilder().setName("snoopy").build();
        UserServiceGrpc.UserServiceBlockingStub stub =
            UserServiceGrpc.newBlockingStub(channel);
        UserInfoResponse userInfoResponse = stub.getUserByName(userInfoRequest);
        log.(userInfoResponse.toString());
    }

}
