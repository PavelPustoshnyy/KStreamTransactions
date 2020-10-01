package kafka;

import entity.Transaction;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MessageSender {
    private Producer<String, String> producer;
    private String topic;

    public MessageSender(String topic) {
        this.producer = KafkaObjectFactory.getProducer();
        this.topic = topic;
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }

    public void send(String value) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,value);
        producer.send(record);
    }

    public void send(String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,key,value);
        producer.send(record);
    }

    public void send(ProducerRecord<String,String> record) {
        producer.send(record);
    }

    public String getTopic() {
        return topic;
    }
}
