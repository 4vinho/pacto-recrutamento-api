package br.com.pacto.recrutamento.web.config;

import br.com.pacto.recrutamento.web.realtime.QuadroCandidaturasSse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@ConditionalOnProperty(name = "realtime.redis.listener-enabled", matchIfMissing = true)
public class RedisRealtimeConfiguration {
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, QuadroCandidaturasSse listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener,
                new ChannelTopic("recrutamento:candidaturas:alteracoes"));
        return container;
    }
}
