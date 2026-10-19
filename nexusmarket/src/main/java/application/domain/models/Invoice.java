package application.domain.models;

import application.domain.exceptions.DomainException;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Commercial document issued when an order is paid. It is a snapshot and is never edited.
 */
public class Invoice extends MarketplaceResource {

    private String invoiceNumber;
    private Order order;
    private Money subtotal;
    private Money taxAmount;
    private Money total;
    private LocalDateTime issuedAt;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Money subtotal) {
        this.subtotal = subtotal;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }


    @Override
    public String getResourceType() {
        return "INVOICE";
    }

    public static Invoice issueFor(Order order, BigDecimal taxRate, String invoiceNumber, LocalDateTime now) {
        if (order == null || order.getId() == null) {
            throw new DomainException("An invoice requires a persisted order.");
        }
        Money subtotal = order.total();
        Money tax = subtotal.percentage(taxRate);
        Invoice invoice = new Invoice();
        invoice.invoiceNumber = invoiceNumber;
        invoice.order = order;
        invoice.subtotal = subtotal;
        invoice.taxAmount = tax;
        invoice.total = subtotal.add(tax);
        invoice.issuedAt = now;
        return invoice;
    }

}
