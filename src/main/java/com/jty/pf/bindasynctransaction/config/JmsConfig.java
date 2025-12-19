package com.jty.pf.bindasynctransaction.config;

import com.jty.pf.bindasynctransaction.jms.JmsSender;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryFactory;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
public class JmsConfig {

    @Value("${spring.artemis.broker-url}")
    private String url;
    @Value("${spring.artemis.user}")
    private String username;
    @Value("${spring.artemis.password}")
    private String password;
    @Value("${spring.artemis.threadPool}")
    private int maxThreadPool;

    @Bean
    public ConnectionFactory connectionFactory() throws JMSException, UnknownHostException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(url);
        connectionFactory.setUser(username);
        connectionFactory.setPassword(password);
        connectionFactory.setThreadPoolMaxSize(maxThreadPool);
//        connectionFactory.setClientID(Inet4Address.getLocalHost().getHostName().concat(LocalDateTime.now().toString()));
        return connectionFactory;
    }

    @Bean
    @Qualifier("jmsListenerContainerFactory")
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
//        factory.setTaskExecutor(); -> 멀티 threadpool
        factory.setConcurrency("2-5");
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }


    @Bean
    public JmsListenerContainerFactory<?> selectorListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer
    ) throws UnknownHostException {
        String selector = "%s=%s".formatted("server", Inet4Address.getLocalHost().getHostName());
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory() {
            @Override
            public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint) {
                DefaultMessageListenerContainer container = super.createListenerContainer(endpoint);
                container.setMessageSelector(selector);
                return container;
            }
        };

        configurer.configure(factory, connectionFactory);
        factory.setConcurrency("2-10");
        factory.setReceiveTimeout(1_000L);
//        factory.setClientId( Inet4Address.getLocalHost().getHostName().concat("selector"));
        return factory;
    }

    @Bean
    public JmsPoolConnectionFactory pooledConnectionFactory(ConnectionFactory connectionFactory) {
        JmsPoolConnectionFactoryProperties properties = new JmsPoolConnectionFactoryProperties();
        properties.setMaxConnections(5);
        properties.setBlockIfFullTimeout(Duration.of(5, ChronoUnit.SECONDS));
        properties.setBlockIfFull(false);
        properties.setEnabled(true);
        properties.setMaxSessionsPerConnection(2);
        JmsPoolConnectionFactoryFactory factory = new JmsPoolConnectionFactoryFactory(properties);
        return factory.createPooledConnectionFactory(connectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(JmsPoolConnectionFactory pooledConnectionFactory) {
        return new JmsTemplate(pooledConnectionFactory);
    }

    @Bean
    public JmsSender jmsSender(JmsTemplate jmsTemplate){
        return new JmsSender(jmsTemplate);
    }

}
