package com.hy.user.grpc;

import com.hy.user.annotations.GrpcService;
import com.hy.user.pb.UserInfo;
import com.hy.user.pb.UserInfoRequest;
import com.hy.user.pb.UserInfoResponse;
import com.hy.user.pb.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

  private Map<String, UserInfo> users = new HashMap<>();

  public UserServiceImpl() {
    UserInfo user = UserInfo.newBuilder()
        .setName("hy")
        .setAge(18)
        .setGender("male")
        .build();
    users.put(user.getName(), user);

    user = UserInfo.newBuilder()
        .setName("panda")
        .setAge(7)
        .setGender("male")
        .build();
    users.put(user.getName(), user);

    user = UserInfo.newBuilder()
        .setName("snoopy")
        .setAge(3)
        .setGender("male")
        .build();
    users.put(user.getName(), user);
  }

  @Override
  public void getUserByName(UserInfoRequest request, StreamObserver<UserInfoResponse> responseObserver) {
    UserInfo user = users.get(request.getName());
    UserInfoResponse response;
    if (user != null) {
      response = UserInfoResponse.newBuilder()
          .setUser(user)
          .setSuccess(true)
          .build();
    } else {
      response = UserInfoResponse.newBuilder()
          .setSuccess(false)
          .setErrMsg("failed to get user " + request.getName())
          .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
