package com.jty.pf.bindasynctransaction.config;

import com.jty.pf.bindasynctransaction.jms.JmsSender;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryFactory;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class JmsConfig {
    @Value("")
    private String url;
    @Value("")
    private String username;
    @Value("")
    private String password;
    @Value("")
    private int maxThreadPool;


    @Bean
    public ConnectionFactory connectionFactory() throws JMSException, UnknownHostException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(url);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        connectionFactory.setMaxThreadPoolSize(maxThreadPool);
        connectionFactory.setClientID(Inet4Address.getLocalHost().getHostName());
        return connectionFactory;
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
//        template.setDeliveryPersistent(Boolean.FALSE);
//        template.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return new JmsTemplate(pooledConnectionFactory);
    }

    @Bean
    public JmsSender jmsSender(JmsTemplate jmsTemplate){
        return new JmsSender(jmsTemplate);
    }

}
