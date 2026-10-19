package application.domain.models;

import application.domain.exceptions.InvalidRefundException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import java.time.LocalDateTime;

/**
 * Money given back to the buyer for a returned product. REQUESTED -> APPROVED | REJECTED.
 * Every decision records who decided and when.
 */
public class Refund extends MarketplaceResource {

    private ProductReturn productReturn;
    private Money amount;
    private RefundStatus status = RefundStatus.REQUESTED;
    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
    private User decidedBy;
    private String decisionNote;

    public ProductReturn getProductReturn() {
        return productReturn;
    }

    public void setProductReturn(ProductReturn productReturn) {
        this.productReturn = productReturn;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(User decidedBy) {
        this.decidedBy = decidedBy;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }


    @Override
    public String getResourceType() {
        return "REFUND";
    }

    public static Refund request(ProductReturn productReturn, Money amount, LocalDateTime now) {
        if (amount == null || !amount.isPositive()) {
            throw new InvalidRefundException("Refund amount must be greater than zero.");
        }
        Refund refund = new Refund();
        refund.productReturn = productReturn;
        refund.amount = amount;
        refund.status = RefundStatus.REQUESTED;
        refund.requestedAt = now;
        return refund;
    }

    public void approve(User decider, String note, LocalDateTime now) {
        decide(RefundStatus.APPROVED, decider, note, now);
    }

    public void reject(User decider, String note, LocalDateTime now) {
        decide(RefundStatus.REJECTED, decider, note, now);
    }

    private void decide(RefundStatus target, User decider, String note, LocalDateTime now) {
        if (!RefundStatus.REQUESTED.equals(status)) {
            throw new InvalidStatusTransitionException("Refund", status, target);
        }
        this.status = target;
        this.decidedBy = decider;
        this.decisionNote = note;
        this.decidedAt = now;
    }

}
