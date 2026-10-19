package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Significant business operation executed over a marketplace resource.
 */
public final class OperationType extends DomainCatalog {

    public static final OperationType BUYER_REGISTRATION = new OperationType(
            "BUYER_REGISTRATION", "Buyer Registration", "Registration of a buyer.");
    public static final OperationType SELLER_REGISTRATION = new OperationType(
            "SELLER_REGISTRATION", "Seller Registration", "Incorporation of a seller by the administrator.");
    public static final OperationType USER_REGISTRATION = new OperationType(
            "USER_REGISTRATION", "User Registration", "Creation of a system user.");
    public static final OperationType USER_STATUS_CHANGE = new OperationType(
            "USER_STATUS_CHANGE", "User Status Change", "Change of a user's access status.");
    public static final OperationType BUYER_STATUS_CHANGE = new OperationType(
            "BUYER_STATUS_CHANGE", "Buyer Status Change", "Change of a buyer's commercial status.");
    public static final OperationType BUYER_ADDRESS_ADDITION = new OperationType(
            "BUYER_ADDRESS_ADDITION", "Buyer Address Addition", "Addition of a delivery address.");
    public static final OperationType BUYER_ADDRESS_REMOVAL = new OperationType(
            "BUYER_ADDRESS_REMOVAL", "Buyer Address Removal", "Removal of a delivery address.");
    public static final OperationType WAREHOUSE_CREATION = new OperationType(
            "WAREHOUSE_CREATION", "Warehouse Creation", "Creation of a warehouse.");
    public static final OperationType WAREHOUSE_UPDATE = new OperationType(
            "WAREHOUSE_UPDATE", "Warehouse Update", "Update of a warehouse.");
    public static final OperationType WAREHOUSE_STATUS_CHANGE = new OperationType(
            "WAREHOUSE_STATUS_CHANGE", "Warehouse Status Change", "Change of a warehouse status.");
    public static final OperationType PRODUCT_REGISTRATION = new OperationType(
            "PRODUCT_REGISTRATION", "Product Registration", "Registration of a product.");
    public static final OperationType PRODUCT_UPDATE = new OperationType(
            "PRODUCT_UPDATE", "Product Update", "Update of a product.");
    public static final OperationType PRODUCT_VARIANT_ADDITION = new OperationType(
            "PRODUCT_VARIANT_ADDITION", "Product Variant Addition", "Addition of a product variant.");
    public static final OperationType PRODUCT_PUBLICATION = new OperationType(
            "PRODUCT_PUBLICATION", "Product Publication", "Publication of a product.");
    public static final OperationType PRODUCT_SUSPENSION = new OperationType(
            "PRODUCT_SUSPENSION", "Product Suspension", "Suspension of a product.");
    public static final OperationType PRODUCT_DISCONTINUATION = new OperationType(
            "PRODUCT_DISCONTINUATION", "Product Discontinuation", "Discontinuation of a product.");
    public static final OperationType INVENTORY_INFLOW = new OperationType(
            "INVENTORY_INFLOW", "Inventory Inflow", "Stock entering a warehouse.");
    public static final OperationType INVENTORY_ADJUSTMENT = new OperationType(
            "INVENTORY_ADJUSTMENT", "Inventory Adjustment", "Manual adjustment of the physical stock.");
    public static final OperationType INVENTORY_DAMAGE_MARKING = new OperationType(
            "INVENTORY_DAMAGE_MARKING", "Inventory Damage Marking", "Inventory marked as damaged.");
    public static final OperationType INVENTORY_AVAILABILITY_RESTORATION = new OperationType(
            "INVENTORY_AVAILABILITY_RESTORATION", "Inventory Availability Restoration", "Inventory marked as available again.");
    public static final OperationType STOCK_RESERVATION = new OperationType(
            "STOCK_RESERVATION", "Stock Reservation", "Stock reserved by an order.");
    public static final OperationType STOCK_RELEASE = new OperationType(
            "STOCK_RELEASE", "Stock Release", "Reserved stock released.");
    public static final OperationType STOCK_SALE = new OperationType(
            "STOCK_SALE", "Stock Sale", "Reserved stock confirmed as sold.");
    public static final OperationType ORDER_CREATION = new OperationType(
            "ORDER_CREATION", "Order Creation", "Creation of an order from the cart.");
    public static final OperationType ORDER_PAYMENT = new OperationType(
            "ORDER_PAYMENT", "Order Payment", "Payment of an order.");
    public static final OperationType ORDER_CANCELLATION = new OperationType(
            "ORDER_CANCELLATION", "Order Cancellation", "Cancellation of an order.");
    public static final OperationType ORDER_DELIVERY = new OperationType(
            "ORDER_DELIVERY", "Order Delivery", "Delivery of an order.");
    public static final OperationType INVOICE_ISSUANCE = new OperationType(
            "INVOICE_ISSUANCE", "Invoice Issuance", "Issuance of an invoice.");
    public static final OperationType SHIPMENT_CREATION = new OperationType(
            "SHIPMENT_CREATION", "Shipment Creation", "Opening of a shipment.");
    public static final OperationType SHIPMENT_DISPATCH = new OperationType(
            "SHIPMENT_DISPATCH", "Shipment Dispatch", "Dispatch of a shipment.");
    public static final OperationType SHIPMENT_DELIVERY = new OperationType(
            "SHIPMENT_DELIVERY", "Shipment Delivery", "Delivery confirmation of a shipment.");
    public static final OperationType RETURN_REQUEST = new OperationType(
            "RETURN_REQUEST", "Return Request", "Request of a return.");
    public static final OperationType RETURN_RECEIPT = new OperationType(
            "RETURN_RECEIPT", "Return Receipt", "Reception of returned goods.");
    public static final OperationType RETURN_REJECTION = new OperationType(
            "RETURN_REJECTION", "Return Rejection", "Rejection of a return.");
    public static final OperationType REFUND_APPROVAL = new OperationType(
            "REFUND_APPROVAL", "Refund Approval", "Approval of a refund.");
    public static final OperationType REFUND_REJECTION = new OperationType(
            "REFUND_REJECTION", "Refund Rejection", "Rejection of a refund.");

    private static final List<OperationType> VALUES = List.of(BUYER_REGISTRATION, SELLER_REGISTRATION, USER_REGISTRATION, USER_STATUS_CHANGE, BUYER_STATUS_CHANGE, BUYER_ADDRESS_ADDITION, BUYER_ADDRESS_REMOVAL, WAREHOUSE_CREATION, WAREHOUSE_UPDATE, WAREHOUSE_STATUS_CHANGE, PRODUCT_REGISTRATION, PRODUCT_UPDATE, PRODUCT_VARIANT_ADDITION, PRODUCT_PUBLICATION, PRODUCT_SUSPENSION, PRODUCT_DISCONTINUATION, INVENTORY_INFLOW, INVENTORY_ADJUSTMENT, INVENTORY_DAMAGE_MARKING, INVENTORY_AVAILABILITY_RESTORATION, STOCK_RESERVATION, STOCK_RELEASE, STOCK_SALE, ORDER_CREATION, ORDER_PAYMENT, ORDER_CANCELLATION, ORDER_DELIVERY, INVOICE_ISSUANCE, SHIPMENT_CREATION, SHIPMENT_DISPATCH, SHIPMENT_DELIVERY, RETURN_REQUEST, RETURN_RECEIPT, RETURN_REJECTION, REFUND_APPROVAL, REFUND_REJECTION);

    private OperationType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OperationType> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static OperationType fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "OperationType");
    }
}
