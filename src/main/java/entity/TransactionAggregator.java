package entity;

public class TransactionAggregator {
    //number of readings per window
    private String ClientPin;
    private int count;
    //sum of the given window
    private double sum;
    //avgerage: computed one time per aggregator
    private double avg;
    public TransactionAggregator() {}

    public int GetCount() { return count;}
    public double GetSum() {return  sum;}
    public double GetAvg() {return  avg;}
    public String GetClientPin() {return  ClientPin;}

    public TransactionAggregator add(Transaction transaction) {
        if(this.ClientPin == null){
            this.ClientPin = transaction.GetClientPin();
        }
        this.count = this.count + 1;
        this.sum = this.sum + transaction.GetReqAmt();
        return this;
    }

    public TransactionAggregator computeAvgReqAmt(){
        this.avg = this.sum/this.count;
        return this;
    }
}
