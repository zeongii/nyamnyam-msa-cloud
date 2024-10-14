package kr.gatewayserver.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient loadBalancedWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .uriBuilderFactory(new DefaultUriBuilderFactory())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)))
                .build();
    }
}
