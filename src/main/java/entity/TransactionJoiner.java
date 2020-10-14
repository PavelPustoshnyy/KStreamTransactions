package entity;

import org.apache.kafka.streams.kstream.ValueJoiner;

public class TransactionJoiner  implements ValueJoiner<Transaction, Transaction, CorrelatedTransaction> {
    @Override
    public CorrelatedTransaction apply(Transaction supermarket, Transaction cafe) {

        CorrelatedTransaction.Builder builder = CorrelatedTransaction.newBuilder();

        String cafeUTime = cafe.GetUTime() != null ? cafe.GetUTime() : null;
        Double cafeReqamt = cafe.GetReqAmt() != null ? cafe.GetReqAmt() : 0.0;
        String cafeMerchant = cafe.GetMerchant() != null ? cafe.GetMerchant() : null;

        String supermarketUTime = supermarket.GetUTime() != null ? supermarket.GetUTime() : null;
        Double supermarketReqamt = supermarket.GetReqAmt() != null ? supermarket.GetReqAmt() : 0.0;
        String supermarketMerchant = supermarket.GetMerchant() != null ? supermarket.GetMerchant() : null;

        String supermarketClientPin = supermarket != null ? supermarket.GetClientPin() : null;
        String cafeClientPin= cafe != null ? cafe.GetClientPin() : null;

        builder.withClientPin(supermarketClientPin != null ? supermarketClientPin : cafeClientPin)
                .withCafeReqAmt(cafeReqamt)
                .withCafeMerchant(cafeMerchant)
                .withCafeUTime(cafeUTime)
                .withSupermarketReqAmt(supermarketReqamt)
                .withSupermarketMerchant(supermarketMerchant)
                .withSupermarketUTime(supermarketUTime);

        return builder.build();
    }
}
