package kafka;

import entity.Transaction;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import serdes.TransactionDeserializer;
import serdes.TransactionSerializer;

import java.util.Properties;

public class KafkaObjectFactory {
    public static Properties getKafkaProperties() {
        Configuration configuration = null;
        try {
            configuration = new PropertiesConfiguration("src/main/resources/application.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        String bootstrapServers = configuration.getString("bootstrap.servers");
        String groupId = configuration.getString("group.id");

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }

    public static Consumer<String, Transaction> getConsumer() {
        Properties properties = getKafkaProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class.getName());
        return new KafkaConsumer<>(properties);
    }



    public static Producer<String, String> getProducer()  {
        Properties properties = getKafkaProperties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<String, String>(properties);
    }


}
