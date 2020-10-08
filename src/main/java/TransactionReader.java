import entity.Transaction;
import entity.TransactionTimestampExtractor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import serdes.SerDeFactory;

import java.util.Properties;

public class TransactionReader {

    public static void main(String[] args) throws Exception {

        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String,Transaction> transactionsKStream = streamsBuilder.stream("first_topic", Consumed.with(stringSerde, transactionSerde))
                //.mapValues(p -> Transaction.builder(p).maskCreditCard().build())
                ;


        transactionsKStream.print(Printed.<String, Transaction>toSysOut().withLabel("check"));
        transactionsKStream.to("analytics", Produced.with(stringSerde,transactionSerde));


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),streamsConfig);

        kafkaStreams.start();
        Thread.sleep(65000);
        kafkaStreams.close();

    }




    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "FirstZmart-Kafka-Streams-Client");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "zmart-purchases");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "FirstZmart-Kafka-Streams-App");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TransactionTimestampExtractor.class);
        return props;
    }

}