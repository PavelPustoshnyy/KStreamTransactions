import entity.Transaction;
import entity.TransactionGenerator;
import kafka.MessageReader;
import kafka.MessageSender;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import serdes.MySerdes;
import serdes2.SerDeFactory;

import java.util.ArrayList;
import java.util.List;


import java.util.Properties;

import static java.lang.System.getProperties;
import static org.apache.kafka.streams.state.RocksDBConfigSetter.LOG;

public class SerializationTest {
    public static void main(String[] args) throws InterruptedException {
        String topic = "first_topic";
        MessageSender sender = new MessageSender(topic);
        TransactionGenerator tg = new TransactionGenerator();
        List<Transaction> transactionList = tg.get(6);
        transactionList.forEach(x->sender.send(x.toString()));
        sender.flush();
        sender.close();

        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-example");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "group-1");

        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsConfig streamsConfig = new StreamsConfig(streamsConfiguration);

        Serde<String> stringSerde = Serdes.String();
        Serde<Transaction> transactionSerde = SerDeFactory.getPOJOSerde(Transaction.class);

        StreamsBuilder builder = new StreamsBuilder();

        //разбиваем на три потока
        KStream<String,Transaction> transactionKStream = builder.stream(topic, Consumed.with(stringSerde, transactionSerde))
                        .mapValues(t -> Transaction.builder(t).maskPin().build())
                ;
        transactionKStream.print( Printed.<String, Transaction>toSysOut().withLabel("coupons"));
        transactionKStream.to("coupons", Produced.with(stringSerde,transactionSerde));

        KafkaStreams kafkaStreams =
                new KafkaStreams(builder.build(),streamsConfig);
        kafkaStreams.start();
    }
}
