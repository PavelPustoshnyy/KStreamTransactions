import entity.Props;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

public class KStreamTransactionsApp {
    public static void main(String[] args) throws InterruptedException {
        TransactionStreamJoin transactionStreamJoin = new TransactionStreamJoin();
        //transactionStreamJoin.run();

    }
}
