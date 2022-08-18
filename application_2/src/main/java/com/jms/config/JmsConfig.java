package com.jms.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;


@EnableJms
@Configuration
public class JmsConfig {
    @Value("${jms.login}")
    private String login;
    @Value("${jms.password}")
    private String password;
    @Value("${jms.host}")
    private String host;

    @Bean
    public DefaultJmsListenerContainerFactory jmsContainerFactory() {
        DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
        containerFactory.setPubSubDomain(true);
        containerFactory.setConnectionFactory(connectionFactory());
        return containerFactory;
    }
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory cachConnectionFactory = new CachingConnectionFactory();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(login, password, host);
        cachConnectionFactory.setTargetConnectionFactory(connectionFactory);
        return cachConnectionFactory;
    }
}
