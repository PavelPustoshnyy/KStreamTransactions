package entity;

import org.apache.kafka.streams.kstream.ValueJoiner;

public class TransactionJoiner  implements ValueJoiner<Transaction, Transaction, CorrelatedTransaction> {
    @Override
    public CorrelatedTransaction apply(Transaction supermarket, Transaction cafe) {

        CorrelatedTransaction.Builder builder = CorrelatedTransaction.newBuilder();

        String cafeUTime = cafe != null ? cafe.GetUTime() : null;
        System.out.println(cafe!=null);
        Double cafeReqamt = cafe != null ? cafe.GetReqAmt() : 0.0;
        String cafeMerchant = cafe != null ? cafe.GetMerchant() : null;

        String supermarketUTime = supermarket != null ? supermarket.GetUTime() : null;
        Double supermarketReqamt = supermarket != null ? supermarket.GetReqAmt() : 0.0;
        String supermarketMerchant = supermarket != null ? supermarket.GetMerchant() : null;

        String supermarketClientPin = supermarket != null ? supermarket.GetClientPin() : null;
        String cafeClientPin= cafe != null ? cafe.GetClientPin() : null;

        builder.withClientPin(supermarketClientPin != null ? supermarketClientPin : cafeClientPin)
                .withCafeReqAmt(cafeReqamt)
                .withSupermarketReqAmt(supermarketReqamt)
                .withCafeMerchant(cafeMerchant)
                .withSupermarketMerchant(supermarketMerchant)
                .withCafeUTime(cafeUTime)
                .withSupermarketUTime(supermarketUTime);

        return builder.build();
    }
}
