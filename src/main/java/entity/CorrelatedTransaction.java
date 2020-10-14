package entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class CorrelatedTransaction {
    private String ClientPin;
    private Double cafeReqAmt;
    private String cafeMerchant;
    private String cafeUTime;
    private Double supermarketReqAmt;
    private String supermarketMerchant;
    private String supermarketUTime;

    private CorrelatedTransaction(Builder builder) {
        ClientPin = builder.ClientPin;
        cafeReqAmt = builder.cafeReqAmt;
        cafeMerchant = builder.cafeMerchant;
        cafeUTime = builder.cafeUTime;
        supermarketReqAmt = builder.supermarketReqAmt;
        supermarketMerchant = builder.supermarketMerchant;
        supermarketUTime = builder.supermarketUTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonAlias("CLIENTPIN")
    @JsonGetter("ClientPin")
    public String getClientPin() { return ClientPin;}
    @JsonAlias("CAFEREQAMT")
    @JsonGetter("cafeReqAmt")
    public Double getCafeReqAmt() { return cafeReqAmt;}
    @JsonAlias("CAFEMERCHANT")
    @JsonGetter("cafeMerchant")
    public String getCafeMerchant() { return cafeMerchant;}
    @JsonAlias("CAFEUTIME")
    @JsonGetter("cafeUTime")
    public String getCafeUTime() { return cafeUTime;}
    @JsonAlias("SUPERMARKETREQAMT")
    @JsonGetter("supermarketReqAmt")
    public Double getSupermarketReqAmt() { return supermarketReqAmt;}
    @JsonAlias("SUPERMARKETMERCHANT")
    @JsonGetter("supermarketMerchant")
    public String getSupermarketMerchant() { return supermarketMerchant;}
    @JsonAlias("SUPERMARKETUTIME")
    @JsonGetter("supermarketUTime")
    public String getSupermarketUTime() { return supermarketUTime;}

    @Override
    public String toString() {
        return "{" +
                "\"CLIENTPIN\":\"" + ClientPin +
                "\",\"CAFEREQAMT\":" + cafeReqAmt +
                ",\"CAFEMERCHANT\":\"" + cafeMerchant +
                "\",\"CAFEUTIME\":\"" + cafeUTime +
                "\",\"SUPERMARKETREQAMT\":" + supermarketReqAmt +
                ",\"SUPERMARKETMERCHANT\":\"" + supermarketMerchant +
                "\",\"SUPERMARKETUTIME\":\"" + supermarketUTime +
                "\"}";
    }

    public static final class Builder {
        private String ClientPin;
        private Double cafeReqAmt;
        private String cafeMerchant;
        private String cafeUTime;
        private Double supermarketReqAmt;
        private String supermarketMerchant;
        private String supermarketUTime;

        private Builder() {
        }

        public Builder withClientPin(String val) {
            ClientPin = val;
            return this;
        }
        public Builder withCafeReqAmt(Double val) {
            cafeReqAmt = val;
            return this;
        }
        public Builder withSupermarketReqAmt(Double val) {
            supermarketReqAmt = val;
            return this;
        }

        public Builder withCafeMerchant(String val) {
            cafeMerchant = val;
            return this;
        }

        public Builder withSupermarketMerchant(String val) {
            supermarketMerchant = val;
            return this;
        }

        public Builder withCafeUTime(String val) {
            cafeUTime = val;
            return this;
        }

        public Builder withSupermarketUTime(String val) {
            supermarketUTime = val;
            return this;
        }

        public CorrelatedTransaction build() {
            return new CorrelatedTransaction(this);
        }
    }
}
