package entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

public class TransactionAggregation {

    private String ClientPin;
    private double AvgReqAmt;

    public TransactionAggregation() {}
    public TransactionAggregation (String ClientPin, Double AvgReqAmt) {
        this.ClientPin = ClientPin;
        this.AvgReqAmt=AvgReqAmt;
    }

    @JsonAlias("CLIENTPIN")
    @JsonSetter("ClientPin")
    public void setClientPin(String clientPin) {
        this.ClientPin=clientPin;
    }

    @JsonAlias("AVGREQAMT")
    @JsonSetter("AvgReqAmt")
    public void setReqAmt(Double AvgReqAmt) {
        this.AvgReqAmt=AvgReqAmt;
    }

    @JsonAlias("CLIENTPIN")
    @JsonGetter("ClientPin")
    public String GetClientPin() {return  this.ClientPin;}

    @JsonAlias("AVGREQAMT")
    @JsonGetter("AvgReqAmt")
    public double GetAvg() {return  this.AvgReqAmt;}


    @Override
    public String toString() {
        return "{\"CLIENTPIN\":\"" + ClientPin +
                "\",\"AVGREQAMT\":" + AvgReqAmt + "}";
    }

}