package entity;

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

    public String getClientPin() { return ClientPin;}
    public Double getCafeReqAmt() { return cafeReqAmt;}
    public String getCafeMerchant() { return cafeMerchant;}
    public String getCafeUTime() { return cafeUTime;}
    public Double getSupermarketReqAmt() { return supermarketReqAmt;}
    public String getSupermarketMerchant() { return supermarketMerchant;}
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
