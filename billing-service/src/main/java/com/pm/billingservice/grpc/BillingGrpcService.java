package com.pm.billingservice.grpc;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase  {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
                                     StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("CreateBillingAccount called with request: {}", billingRequest.toString());
        //Bussiness Logic - eg save to database , perform calculation
        BillingResponse billingResponse =  BillingResponse.newBuilder()
                .setAccountId("12345")
                 .setStatus("SUCCESS")
                .build();
        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}
