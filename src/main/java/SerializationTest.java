import entity.*;
import kafka.MessageSender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import serdes2.SerDeFactory;

import java.util.List;

import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.LATEST;

public class SerializationTest {
    public static void main(String[] args) throws InterruptedException {
        String topic = "first_topic";
        MessageSender sender = new MessageSender(topic);
        TransactionGenerator tg = new TransactionGenerator();
        List<Transaction> transactionList = tg.get(1000);
        transactionList.forEach(x->sender.send(x.toString()));
        sender.flush();
        sender.close();

        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Serde<String> stringSerde = Serdes.String();
        Serde<TransactionAverage> transactionKeySerde = SerDeFactory.getPOJOSerde(TransactionAverage.class);
        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String,Transaction> transactionKStream = builder.stream(topic, Consumed.with(stringSerde, transactionSerde));

        KStream<String, Transaction> filteredKStream = transactionKStream.filter((key, transaction) -> transaction.GetReqAmt() > 0.00);
        Predicate<String, Transaction> isCommerce = (key, transaction) -> transaction.GetMerchant().equals("E-Commerce");
        Predicate<String, Transaction> isSupermarkets = (key, transaction) -> transaction.GetMerchant().equals("Supermarkets");
        Predicate<String, Transaction> isCafe = (key, transaction) -> transaction.GetMerchant().equals("Cafe&Restaraunt");

        int commerce = 0;
        int supermarkets = 1;
        int cafe = 2;

        KStream<String, Transaction>[] branchesStream = filteredKStream
                .selectKey((k,v)-> v.GetClientPin())
                .branch(isCommerce, isSupermarkets, isCafe);

        KStream<String, Transaction> cafeStream = branchesStream[cafe];
        KStream<String, Transaction> supermarketsStream = branchesStream[supermarkets];

        ValueJoiner<Transaction, Transaction, CorrelatedTransaction> transactionJoiner = new TransactionJoiner();
        JoinWindows fortyFiveMinuteWindow =  JoinWindows.of(60 * 1000 * 45);

        KStream<String, CorrelatedTransaction> joinedKStream = cafeStream.join(supermarketsStream,
                transactionJoiner,
                fortyFiveMinuteWindow,
                Joined.with(stringSerde,
                        transactionSerde,
                        transactionSerde));

        joinedKStream.print(Printed.<String, CorrelatedTransaction>toSysOut().withLabel("joined KStream"));

        long fortyFiveMinutes = 1000 * 60 * 45;
        long fiveSeconds = 1000 * 5;
        KTable<Windowed<String>, AvgValue> tempTable = branchesStream[cafe].<AvgValue, TimeWindow>aggregateByKey(
                () -> new AvgAggregator<String, Integer, AvgValue>(),
                TimeWindow.of("avgWindow").with(10000),
                new StringSerializer(), new AvgValueSerializer(),
                new StringDeserializer(), new AvgValueDeserializer());

        KTable<Windowed<String>, Double> avg = tempTable.<Double>mapValues((v) -> ((double) v.sum / v.count));


        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
        kafkaStreams.start();
        Thread.sleep(65000);
        kafkaStreams.close();

    }
    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "join_driver_application");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-fifth-application");
        //props.put(ConsumerConfig.CLIENT_ID_CONFIG, "join_driver_client");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1");
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "10000");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TransactionTimestampExtractor.class);
        return props;
    }

}
