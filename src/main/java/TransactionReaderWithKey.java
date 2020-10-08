import entity.Transaction;
import entity.TransactionTimestampExtractor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import serdes.SerDeFactory;

import java.util.Properties;

public class TransactionReaderWithKey {
    public static void main(String[] args) throws Exception {

        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder streamsBuilder = new StreamsBuilder();
                ;
        KeyValueMapper<String, Transaction, KeyValue<String,Transaction>> custIdCCMasking = (k, v) -> {
            Transaction masked = Transaction.builder(v).maskPin().build();
            return new KeyValue<>(masked.GetClientPin(), masked);
        };

        Predicate<String, Transaction> isSupermarkets = (key, transaction) -> transaction.GetMerchant().equals("Supermarkets");
        Predicate<String, Transaction> isCafe = (key, transaction) -> transaction.GetMerchant().equals("Cafe&Restaraunt");

        int supermarkets = 0;
        int cafe = 1;
        String inTopic = "first_topic";

        KStream<String, Transaction> transactionStream = streamsBuilder.stream( inTopic, Consumed.with(Serdes.String(), transactionSerde)).map(custIdCCMasking);

        KStream<String, Transaction>[] branchesStream = transactionStream.selectKey((k,v)-> v.GetClientPin()).branch(isSupermarkets,isCafe);

        KStream<String, Transaction> supermarketStream = branchesStream[supermarkets];
        KStream<String, Transaction> cafeStream = branchesStream[cafe];


        supermarketStream.print(Printed.<String, Transaction>toSysOut().withLabel("supermarkets"));
        supermarketStream.to("analytics", Produced.with(stringSerde,transactionSerde));
        cafeStream.print(Printed.<String, Transaction>toSysOut().withLabel("cafe"));
        cafeStream.to("analytics", Produced.with(stringSerde,transactionSerde));


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),streamsConfig);

        kafkaStreams.start();
        Thread.sleep(65000);
        kafkaStreams.close();

    }




    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "join_driver_application");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "join_driver_group");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "join_driver_client");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1");
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "10000");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TransactionTimestampExtractor.class);
        return props;
    }
}
