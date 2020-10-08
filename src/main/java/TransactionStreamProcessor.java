import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import entity.Transaction;
import entity.TransactionAggregator;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.*;
import serdes.JsonPOJOSerializer;
import serdes.JsonPOJODeserializer;
import serdes.SerDeFactory;


public class TransactionStreamProcessor {

    static public final class AverageReadingSerde extends Serdes.WrapperSerde<TransactionAggregator> {

        public AverageReadingSerde() {
            super(new JsonPOJOSerializer<TransactionAggregator>(),
                    new JsonPOJODeserializer<TransactionAggregator>(TransactionAggregator.class));
        }
    }

    public static void main(String[] args){

        //Kafka Stream Props
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sensor-processor");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "processor-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        // Where to find the Confluent schema registry instance(s)
        //props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Transaction> source = builder.stream("first_topic");

        KGroupedStream<String, Transaction> groupedTransactionReading = source.groupBy((k,v)->v.GetClientPin());

        KStream<String, TransactionAggregator>  windowedAverageStream = groupedTransactionReading.aggregate(
                () -> new TransactionAggregator(),
                (k, v, t) -> t.add(v),
                Materialized.with(Serdes.String(), SerDeFactory.getPOJOSerde(TransactionAggregator.class)))
                .toStream()
                .mapValues(v -> v.computeAvgReqAmt());

        KStream<String, TransactionAggregator> printStream = windowedAverageStream.peek(
                new ForeachAction<String, TransactionAggregator>() {
                    @Override
                    public void apply(String key, TransactionAggregator value) {
                        System.out.println("ClientPin=" + key + ", Average=" + value.GetAvg());
                    }
                });

        // tempTable.to(Serdes.String(), double or AverageReading);

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("sensor-anon-processor-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            System.out.println("Starting............");
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}
