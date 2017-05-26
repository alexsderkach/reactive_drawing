package io.alexsderkach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxProcessor;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Alex Derkach
 */
@EnableWebFlux
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public HandlerMapping webSocketMapping() {
        FluxProcessor<String, String> processor = DirectProcessor.create();
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(of("/events", (WebSocketHandler) session -> {
            session.receive().map(WebSocketMessage::getPayloadAsText).subscribe(processor);
            return session.send(processor.map(session::textMessage));
        }));
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
