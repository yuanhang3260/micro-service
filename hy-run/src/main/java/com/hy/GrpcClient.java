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
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GrpcClient {

    private static final String localHost = "127.0.0.1";

    public static void main(String[] args) {
        String userName = args[0];
        int parallel = Integer.valueOf(args[1]);
        getUser(userName, parallel);
    }

    private static ManagedChannel getChannel(int port) throws Exception {
        return ManagedChannelBuilder.forAddress(localHost, port)
            .disableRetry()
            .idleTimeout(2, TimeUnit.SECONDS)
            .usePlaintext()
            .build();

//        NettyChannelBuilder ncb = NettyChannelBuilder.forAddress(localHost, port).sslContext(SslContextBuilder.forClient()
//            .sslProvider(OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK)
//            .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
//            .trustManager(InsecureTrustManagerFactory.INSTANCE)
//            .applicationProtocolConfig(
//                new ApplicationProtocolConfig(
//                    ApplicationProtocolConfig.Protocol.ALPN,
//                    ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
//                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
//                    ApplicationProtocolNames.HTTP_2,
//                    ApplicationProtocolNames.HTTP_1_1))
//            .build());
//        return ncb.usePlaintext().build();
    }

    private static void getUser(String username, int parallel) {
        try {
            ManagedChannel channel = getChannel(9092);
            UserInfoRequest userInfoRequest =
                UserInfoRequest.newBuilder().setName(username).build();
            UserServiceGrpc.UserServiceBlockingStub stub =
                UserServiceGrpc.newBlockingStub(channel);

            ExecutorService executor = Executors.newFixedThreadPool(200);

            List<Future> futures = new ArrayList<>();
            for (int i = 0; i < parallel; i++) {
                futures.add(executor.submit(() -> {
                    return stub.getUserByName(userInfoRequest);
                }));
            }

            List<UserInfoResponse> results = new ArrayList<>();
            for (int i = 0; i < parallel; i++) {
                results.add((UserInfoResponse) futures.get(i).get());
            }

            boolean success = true;
            for (int i = 1; i < parallel; i++) {
                if (!results.get(0).equals(results.get(i))) {
                    success = false;
                    break;
                }
            }

            System.out.println("success: " + success);
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
