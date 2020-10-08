package entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

@CustomAnnotation
public class Transaction {
    private String ClientPin;
    private Double ReqAmt;
    private String Merchant;
    private String UTime;

    public Transaction(){}


    public Transaction(String ClientPin, Double ReqAmt, String Merchant, String UTime) {
        this.ClientPin=ClientPin;
        this.ReqAmt=ReqAmt;
        this.Merchant=Merchant;
        this.UTime=UTime;
    }

    private Transaction(Builder builder) {
        ClientPin = builder.ClientPin;
        ReqAmt = builder.ReqAmt;
        Merchant = builder.Merchant;
        UTime = builder.UTime;
    }
    public static Builder builder() {
        return new Builder();
    }


    public static Builder builder(Transaction copy) {
        Builder builder = new Builder();
        builder.ClientPin = copy.ClientPin;
        builder.ReqAmt = copy.ReqAmt;
        builder.Merchant = copy.Merchant;
        builder.UTime = copy.UTime;
        return builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String GetClientPin() {
        return this.ClientPin;
    }
    public Double GetReqAmt() {
        return this.ReqAmt;
    }
    public String GetMerchant() {
        return this.Merchant;
    }
    public String GetUTime() {
        return this.UTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction transaction = (Transaction) o;

        if (ClientPin != transaction.ClientPin) return false;
        if (Double.compare(transaction.ReqAmt, ReqAmt) != 0) return false;
        if (Merchant != transaction.Merchant) return false;
        if (UTime != transaction.UTime) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ClientPin != null ?  ClientPin.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "{\"CLIENTPIN\":\"" + ClientPin +
                "\",\"REQAMT\":" + ReqAmt +
                ",\"MERCHANT\":\"" + Merchant +
                "\",\"UTIME\":\"" + UTime + "\"}";
    }


    public static final class Builder {
        private String ClientPin;
        private Double ReqAmt;
        private String Merchant;
        private String UTime;

        private static final String CC_NUMBER_REPLACEMENT="xxxxxx";

        private Builder() {
        }

        public Builder ClientPin(String val) {
            ClientPin = val;
            return this;
        }

        public Builder maskPin(){
            Objects.requireNonNull(this.ClientPin, "Client Pin can't be null");
            this.ClientPin = "xxxxxx";
            return this;
        }

        public Builder ReqAmt(Double ReqAmt) {
            this.ReqAmt = ReqAmt;
            return this;
        }

        public Builder Merchant(String Merchant) {
            this.Merchant = Merchant;
            return this;
        }

        public Builder UTime(String UTime) {
            this.UTime = UTime;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
    @JsonAlias("CLIENTPIN")
    @JsonSetter("ClientPin")
    public void setClientPin(String clientPin) {
        this.ClientPin=clientPin;
    }

    @JsonAlias("REQAMT")
    @JsonSetter("ReqAmt")
    public void setReqAmt(Double reqAmt) {
        this.ReqAmt=reqAmt;
    }

    @JsonAlias("MERCHANT")
    @JsonSetter("Merchant")
    public void setMerchant(String Merchant) {
        this.Merchant=Merchant;
    }

    @JsonAlias("UTIME")
    @JsonSetter("UTime")
    public void setUTime(String UTime) {
        this.UTime=UTime;
    }


}
