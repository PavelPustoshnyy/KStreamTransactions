import entity.Transaction;
import entity.TransactionGenerator;
import kafka.MessageReader;
import kafka.MessageSender;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import serdes.MySerdes;

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

        StreamsConfig streamsConfig = new StreamsConfig(new Properties());

        Serde<String> stringSerde = new MySerdes.StringSerde();
        Serde<Transaction> transactionSerde = new MySerdes.TransactionSerde();

        StreamsBuilder builder = new StreamsBuilder();

        //разбиваем на три потока
        KStream<String,Transaction> transactionKStream = builder.stream(topic, Consumed.with(stringSerde, transactionSerde))
                        .mapValues(t -> Transaction.builder(t).maskPin().build());
        transactionKStream.print( Printed.<String, Transaction>toSysOut().withLabel("coupons"));
        transactionKStream.to("coupons", Produced.with(stringSerde,transactionSerde));

        KafkaStreams kafkaStreams =
                new KafkaStreams(builder.build(),streamsConfig);
        kafkaStreams.start();
    }
}
