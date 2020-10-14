package entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class TransactionAggregator {
    private String ClientPin;
    private Long count;
    private double sum;
    public TransactionAggregator() {}

    @JsonAlias("CLIENTPIN")
    @JsonSetter("ClientPin")
    public void setClientPin(String clientPin) {
        this.ClientPin=clientPin;
    }

    @JsonAlias("CLIENTPIN")
    @JsonGetter("ClientPin")
    public String getClientPin() {
        return this.ClientPin;
    }

    @JsonAlias("COUNT")
    @JsonSetter("count")
    public void setCount(Long newCount) {
        this.count=newCount;
    }

    @JsonAlias("COUNT")
    @JsonGetter("count")
    public Long getCount() {
        return this.count;
    }

    @JsonAlias("SUM")
    @JsonSetter("sum")
    public void setSum(Double newSum) {
        this.sum=newSum;
    }

    @JsonAlias("SUM")
    @JsonGetter("sum")
    public Double getSum() {
        return this.sum;
    }

    @Override
    public String toString() {
        return "{\"CLIENTPIN\":\"" + ClientPin +
                "\",\"COUNT\":" + this.count +
                ",\"SUM\":" + this.sum + "}";
    }
}
