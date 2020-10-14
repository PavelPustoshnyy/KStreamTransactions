import entity.*;
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

public class TransactionStreamJoin {
    public static void main(String[] args) throws InterruptedException {
        Props props = new Props();
        StreamsConfig streamsConfig = new StreamsConfig(props.getProperties());
        StreamsBuilder builder = new StreamsBuilder();

        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);
        Serde<CorrelatedTransaction> correlatedTransactionSerde = SerDeFactory.getPOJOSerde(CorrelatedTransaction.class);
        Serde<String> stringSerde = Serdes.String();

        KeyValueMapper<String, Transaction, KeyValue<String,Transaction>> clientPinMasking = (k, v) -> {
            String key = v.GetClientPin();
            Transaction masked = Transaction.builder(v).maskPin().build();
            return new KeyValue<>(key, masked);
        };

        Predicate<String, Transaction> isSupermarkets = (key, transaction) -> transaction.GetMerchant().equals("Supermarkets");
        Predicate<String, Transaction> isCafe = (key, transaction) -> transaction.GetMerchant().equals("Cafe&Restaraunt");

        int supermarkets = 0;
        int cafe = 1;
        String inTopic = "first_topic";

        KStream<String, Transaction> transactionStream = builder
                .stream(inTopic, Consumed.with(Serdes.String(), transactionSerde))
                .filter((key, transaction) -> transaction.GetReqAmt() <= 0)
                ;

        KStream<String, Transaction>[] branchesStream = transactionStream
                .selectKey((k,v)-> v.GetClientPin())
                .map(clientPinMasking)
                .branch(isSupermarkets,isCafe);

        KStream<String, Transaction> supermarketStream = branchesStream[supermarkets];
        KStream<String, Transaction> cafeStream = branchesStream[cafe];

        ValueJoiner<Transaction, Transaction, CorrelatedTransaction> transactionJoiner = new TransactionJoiner();
        JoinWindows window =  JoinWindows.of(60 * 1000 * 45);

        KStream<String, CorrelatedTransaction> joinedKStream = supermarketStream.join(cafeStream,
                transactionJoiner,
                window,
                Joined.with(stringSerde,
                        transactionSerde,
                        transactionSerde));

        joinedKStream.print(Printed.<String, CorrelatedTransaction>toSysOut().withLabel("joined KStream"));
        joinedKStream.to("coupons", Produced.with(stringSerde,correlatedTransactionSerde));


        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
        kafkaStreams.start();
        Thread.sleep(65000);
        kafkaStreams.close();

    }
}