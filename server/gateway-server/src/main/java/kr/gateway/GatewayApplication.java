package kr.gateway;

import lombok.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Bean
    public CommandLineRunner testMongoConnection(ReactiveMongoTemplate mongoTemplate) {



        return args -> {
            // "test" 데이터베이스에 "chatRooms" 컬렉션이 있는지 확인
            String databaseName = "test";
            String collectionName = "chatRooms"; // 확인할 컬렉션 이름

            // MongoDB에서 "test" 데이터베이스에 "chatRooms" 컬렉션이 있는지 확인
            Mono<Boolean> collectionExists = mongoTemplate.collectionExists(collectionName);

            // 결과를 출력
            collectionExists.subscribe(exists -> {
                if (exists) {
                    System.out.println("MongoDB의 '" + databaseName + "' 데이터베이스에 '" + collectionName + "' 컬렉션이 존재합니다.");
                } else {
                    System.out.println("MongoDB의 '" + databaseName + "' 데이터베이스에 '" + collectionName + "' 컬렉션이 없습니다.");
                }
            }, error -> {
                System.out.println("MongoDB 연결에 실패했습니다: " + error.getMessage());
            });
        };
    }
}
