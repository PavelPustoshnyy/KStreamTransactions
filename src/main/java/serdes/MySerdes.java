package serdes;

import entity.Transaction;
import org.apache.kafka.common.serialization.*;

import java.util.Map;

public class MySerdes {
    static protected class WrapperSerde<T> implements Serde<T> {
        final private Serializer<T> serializer;
        final private Deserializer<T> deserializer;

        public WrapperSerde(Serializer<T> serializer, Deserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            serializer.configure(configs, isKey);
            deserializer.configure(configs, isKey);
        }

        @Override
        public void close() {
            serializer.close();
            deserializer.close();
        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }

    }
    static public final class TransactionSerde extends WrapperSerde<Transaction> {
        public TransactionSerde() {
            super(new TransactionSerializer(), new TransactionDeserializer());
        }
    }
    static public final class StringSerde extends WrapperSerde<String> {
        public StringSerde() {
            super(new StringSerializer(), new StringDeserializer());
        }
    }
    @SuppressWarnings("unchecked")
    static public <T> Serde<T> serdeFrom(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return (Serde<T>) String();
        }
        if (Transaction.class.isAssignableFrom(type)) {
            return (Serde<T>)  Transaction();
        }
        throw new IllegalArgumentException("Unknown class for built-in serializer");
    }
    static public <T> Serde<T> serdeFrom(final Serializer<T> serializer, final Deserializer<T> deserializer) {
        if (serializer == null) {
            throw new IllegalArgumentException("serializer must not be null");
        }
        if (deserializer == null) {
            throw new IllegalArgumentException("deserializer must not be null");
        }

        return new WrapperSerde<T>(serializer, deserializer);
    }
    static public Serde<Transaction> Transaction() {
        return new TransactionSerde();
    }
    static public Serde<String> String() {
        return new StringSerde();
    }
}