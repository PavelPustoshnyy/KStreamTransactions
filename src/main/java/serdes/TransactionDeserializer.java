package serdes;

import entity.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionDeserializer extends JsonPOJODeserializer<Transaction> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("JsonPOJOClass", Transaction.class);
        super.configure(serdeProps, false);
    }
}
