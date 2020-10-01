package entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class TransactionGenerator {
    ArrayList<String> ClientPinValues;
    ArrayList<String> MerchantValues;
    Double minValue;
    Double maxValue;
    int deviation;

    public TransactionGenerator() {
        this.ClientPinValues = new ArrayList<String>();
        this.ClientPinValues.add("oneone");
        this.ClientPinValues.add("twotwo");
        this.ClientPinValues.add("threee");
        this.minValue = -1000.0;
        this.maxValue = 1000.0;
        this.deviation = 300;
        this.MerchantValues= new ArrayList<String>();
        this.MerchantValues.add("Cafe&Restaraunt");
        this.MerchantValues.add("E-Commerce");
        this.MerchantValues.add("Supermarkets");
    }

    public TransactionGenerator(ArrayList<String> ClientPinValues,
                                ArrayList<String> MerchantValues,
                                Double minValue,
                                Double maxValue,
                                int deviation) {
        this.ClientPinValues = ClientPinValues;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.deviation = deviation;
        this.MerchantValues = MerchantValues;
    }

    private String GenerateClientPinValue() {
        Random rand = new Random();
        int ClientPinRandom = rand.nextInt(ClientPinValues.size());
        String PinValue = ClientPinValues.get(ClientPinRandom);
        return PinValue;
    }

    private Double GenerateReqAmt() {
        return Math.random() * (maxValue - minValue + 1) + minValue;
    }

    private String GenerateMerchant() {
        Random rand = new Random();
        int MerchantValueRandom = rand.nextInt(MerchantValues.size());
        String MerchantValue = MerchantValues.get(MerchantValueRandom);
        return MerchantValue;
    }

    private String GenerateUtime() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis()+deviation);
        return formatter.format(date);
    }

    public Transaction get() {
        return new Transaction(GenerateClientPinValue(),GenerateReqAmt(),GenerateMerchant(),GenerateUtime());
    }

    public List<Transaction> get(int n) {
        List<Transaction> transactionList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            transactionList.add(get());
        }
        return transactionList;
    }
}
