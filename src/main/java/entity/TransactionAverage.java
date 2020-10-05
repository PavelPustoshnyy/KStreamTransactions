package entity;

public class TransactionAverage {
    private String ClientPin;
    private Double avgReqAmt;

    public TransactionAverage(String ClientPin) {
        this.ClientPin = ClientPin;
    }

    public void setAverage(Double avgReqAmt){
        this.avgReqAmt = avgReqAmt;
    }

    public String getClientPin() {
        return ClientPin;
    }

    public Double getAvgReqAmt() {
        return avgReqAmt;
    }

    public static TransactionAverage from(Transaction transaction){
        return new TransactionAverage(transaction.GetClientPin());
    }

    @Override
    public String toString() {
        return "{\"CLIENTPIN\":\"" + ClientPin +
                "\",\"AVGREQAMT\":" + avgReqAmt + "}";
    }
}
