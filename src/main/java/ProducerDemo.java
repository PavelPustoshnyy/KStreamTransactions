import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    String bootstrapServers;
    Properties properties;
    String topic;
    String message;
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    public ProducerDemo(String bootstrapServers,String topic, String message) {
        this.topic=topic;
        this.bootstrapServers =bootstrapServers;
        this.properties= new Properties();
        this.message=message;
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer .class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer .class.getName());

        // create the producer
        log.info("creating producer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //create a producer record
        log.info("creating record");
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,"a key",message);

        // send data - asynchronous
        log.info("sending record");
        producer.send(record);

        // Flush data
        log.info("flushing");
        producer.flush();
        // flush and close producer
        log.info("closing");
        producer.close();
        log.info("done");

    }
}
