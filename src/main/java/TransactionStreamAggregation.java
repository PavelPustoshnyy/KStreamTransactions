import entity.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.*;
import static org.apache.kafka.streams.kstream.Grouped.with;

import serdes.SerDeFactory;

import java.util.Properties;

public class TransactionStreamAggregation {

    public static void main(String[] args) throws InterruptedException {
        Props props = new Props();
        Properties p = props.getProperties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregation_driver_application");
        StreamsConfig streamsConfig = new StreamsConfig(p);

        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);
        Serde<TransactionAggregator> transactionAggregatorSerde = SerDeFactory.getPOJOSerde(TransactionAggregator.class);
        Serde<TransactionAggregation> transactionAggregationSerde = SerDeFactory.getPOJOSerde(TransactionAggregation.class);
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        Predicate<String, Transaction> isCommerce = (key, transaction) -> transaction.GetMerchant().equals("E-Commerce");

        KStream<String,Transaction> transactionsKStream  = streamsBuilder
                .stream("first_topic", Consumed.with(stringSerde, transactionSerde))
                .filter((key, transaction) -> transaction.GetReqAmt() <= 0)
                .branch(isCommerce) [0];
                ;
        long twentySeconds = 1000 * 20;
        long fortyFiveMinutes = 1000 * 60 * 15;
        long fiveSeconds = 1000 * 20;

        TimeWindowedKStream<String, Transaction> aggregationByClientPin = transactionsKStream
                .map((key, transaction) -> new KeyValue<>(transaction.GetClientPin(),
                        transaction))
                .groupByKey(with(stringSerde, transactionSerde))
                .windowedBy(TimeWindows.of(twentySeconds).advanceBy(fiveSeconds).until(fortyFiveMinutes));


        final KTable<Windowed<String>, TransactionAggregator> transactionCountAndSum =
                aggregationByClientPin

                        .aggregate(() -> new TransactionAggregator(),
                        (key, value, aggregate) -> {
                            aggregate.setClientPin(Transaction.builder(value).maskPin().build().GetClientPin());
                            aggregate.setCount(aggregate.getCount() + 1);
                            aggregate.setSum(aggregate.getSum() + value.GetReqAmt());
                            return aggregate;
                        }
                ,Materialized.with(stringSerde, transactionAggregatorSerde)
                )
                ;

        final KStream<String, TransactionAggregation> transactionAverage =
                transactionCountAndSum.toStream()
                .map((window,value) ->  KeyValue.pair(window.key(),
                        new TransactionAggregation(value.getClientPin(),
                                value.getSum() / value.getCount())));

        transactionAverage.print(Printed.<String, TransactionAggregation>toSysOut().withLabel("aggregated KStream"));
        transactionAverage.to("analytics", Produced.with(stringSerde,transactionAggregationSerde));

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),streamsConfig);

        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(65000);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

    }

}
