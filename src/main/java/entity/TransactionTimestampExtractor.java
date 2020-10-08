package entity;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.util.Date;

public class TransactionTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        Transaction transactionTransaction = (Transaction) record.value();
        return java.sql.Timestamp.valueOf(transactionTransaction.GetUTime()).getTime();
    }
}
