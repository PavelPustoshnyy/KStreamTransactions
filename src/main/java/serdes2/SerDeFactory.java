package serdes2;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import serdes.JsonPOJODeserializer;
import serdes.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.Map;

public class SerDeFactory {
    public static <T> Serde<T> getPOJOSerde(Class T) {
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<T> serializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", T);
        serializer.configure(serdeProps, false);

        final Deserializer<T> deserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", T);
        deserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(serializer, deserializer);
    }

}
