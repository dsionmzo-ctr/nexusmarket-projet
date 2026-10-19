package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Payment method accepted by the marketplace.
 */
public final class PaymentMethod extends DomainCatalog {

    public static final PaymentMethod CREDIT_CARD = new PaymentMethod(
            "CREDIT_CARD", "Credit Card", "Payment with credit card.");
    public static final PaymentMethod DEBIT_CARD = new PaymentMethod(
            "DEBIT_CARD", "Debit Card", "Payment with debit card.");
    public static final PaymentMethod BANK_TRANSFER = new PaymentMethod(
            "BANK_TRANSFER", "Bank Transfer", "Payment by bank transfer.");
    public static final PaymentMethod DIGITAL_WALLET = new PaymentMethod(
            "DIGITAL_WALLET", "Digital Wallet", "Payment with a digital wallet.");

    private static final List<PaymentMethod> VALUES = List.of(CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, DIGITAL_WALLET);

    private PaymentMethod(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<PaymentMethod> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static PaymentMethod fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "PaymentMethod");
    }
}
