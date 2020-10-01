import entity.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serdes.TransactionDeserializer;

import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    String bootstrapServers;
    String groupId;
    String topic;
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    Properties properties;
    KafkaConsumer<String, Transaction> consumer;

    public ConsumerDemo(String bootstrapServers, String groupId, String topic,Deserializer<Transaction> deserializerTransaction) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.topic = topic;

        // create consumer configs
        this.properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // create a consumer
        log.info("creating consumer");
        this.consumer = new KafkaConsumer<>(properties);

        // subscribe consumer to our topic(s)
        log.info("subscribing");
        consumer.subscribe(Collections.singleton(topic));
        log.info("done creating");
    }

}
