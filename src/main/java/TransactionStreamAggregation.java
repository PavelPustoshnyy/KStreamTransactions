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

public class TransactionStreamAggregation {

    public static void main(String[] args) throws InterruptedException {
        Props props = new Props();
        StreamsConfig streamsConfig = new StreamsConfig(props.getProperties());

        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);
        Serde<TransactionAggregator> transactionAggregatorSerde = SerDeFactory.getPOJOSerde(TransactionAggregator.class);
        Serde<TransactionAggregation> transactionAggregationSerde = SerDeFactory.getPOJOSerde(TransactionAggregation.class);
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String,Transaction> transactionsKStream = streamsBuilder
                .stream("first_topic", Consumed.with(stringSerde, transactionSerde))
                .filter((key, transaction) -> transaction.GetReqAmt() <= 0)
                ;

        //KGroupedStream<String, Transaction> groupedTransactionReading = transactionsKStream.groupBy((k, v) -> v.GetClientPin());

        //KStream<String, TransactionAggregator> windowedAverageStream = groupedTransactionReading.aggregate(
        //       () -> new TransactionAggregator(),
        //       (k, v, t) -> t.add(v),
        //       Materialized.with(Serdes.String(), SerDeFactory.getPOJOSerde(TransactionAggregator.class)))
        //       .toStream()
        //       .mapValues(v -> v.computeAvgReqAmt());

        //windowedAverageStream.print(Printed.<String, TransactionAggregator>toSysOut().withLabel("check"));

        KGroupedStream<String, Transaction> aggregationByClientPin = transactionsKStream
                .map((key, transaction) -> new KeyValue<>(transaction.GetClientPin(),
                        transaction))
                .groupByKey(with(stringSerde, transactionSerde));

        final KTable<String, TransactionAggregator> transactionCountAndSum =
                aggregationByClientPin.aggregate(() -> new TransactionAggregator(),
                        (key, value, aggregate) -> {
                            aggregate.setClientPin(value.GetClientPin());
                            aggregate.setCount(aggregate.getCount() + 1);
                            aggregate.setSum(aggregate.getSum() + value.GetReqAmt());
                            return aggregate;
                        }
                ,Materialized.with(stringSerde, transactionAggregatorSerde)
                )
                ;

        final KTable<String, TransactionAggregation> transactionAverage =
                transactionCountAndSum.mapValues(value -> new TransactionAggregation(value.getClientPin(),
                                value.getSum() / value.getCount()),
                       Materialized.with(stringSerde, transactionAggregationSerde));

        // persist the result in topic
        transactionAverage.toStream().print(Printed.<String, TransactionAggregation>toSysOut().withLabel("aggregated KStream"));
        transactionAverage.toStream().to("analytics", Produced.with(stringSerde,transactionAggregationSerde));
        //transactionCountAndSum.toStream().to("analytics", Produced.with(stringSerde,transactionAggregatorSerde));
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),streamsConfig);
//
        kafkaStreams.start();
        Thread.sleep(65000);
        kafkaStreams.close();

    }

}
