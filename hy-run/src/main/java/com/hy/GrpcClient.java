package com.hy;

import com.hy.user.pb.UserInfoRequest;
import com.hy.user.pb.UserInfoResponse;
import com.hy.user.pb.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.concurrent.TimeUnit;

public class GrpcClient {

    private static final String localHost = "127.0.0.1";

    public static void main(String[] args) {
        String userName = args[0];
        getUser(userName);
    }

    private static ManagedChannel getChannel(int port) throws Exception {
        return ManagedChannelBuilder.forAddress(localHost, port)
            .disableRetry()
            .idleTimeout(2, TimeUnit.SECONDS)
            .usePlaintext()
            .build();
    }

    private static void getUser(String username) {
        try {
            ManagedChannel channel = getChannel(9092);
            UserInfoRequest userInfoRequest =
                UserInfoRequest.newBuilder().setName(username).build();
            UserServiceGrpc.UserServiceBlockingStub stub =
                UserServiceGrpc.newBlockingStub(channel);
            UserInfoResponse userInfoResponse = stub.getUserByName(userInfoRequest);
            System.out.println(userInfoResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
