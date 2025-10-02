package ru.yandex.practicum.telemetry.analyzer.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.hub-router.address}")
    private String hubRouterAddress;

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(hubRouterAddress)
                .usePlaintext()
                .build();

        return HubRouterControllerGrpc.newBlockingStub(channel);
    }
}