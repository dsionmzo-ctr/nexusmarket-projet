package application.domain.models;

/**
 * Outcome of a return request: the return and the refund opened with it.
 */
public class ReturnRequestResult {

    private ProductReturn productReturn;
    private Refund refund;

    public ReturnRequestResult() {
    }

    public ReturnRequestResult(ProductReturn productReturn, Refund refund) {
        this.productReturn = productReturn;
        this.refund = refund;
    }

    public ProductReturn getProductReturn() {
        return productReturn;
    }

    public void setProductReturn(ProductReturn productReturn) {
        this.productReturn = productReturn;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

}
