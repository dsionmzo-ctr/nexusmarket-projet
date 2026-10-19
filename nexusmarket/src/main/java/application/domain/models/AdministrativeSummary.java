package application.domain.models;

import application.domain.valueobjects.Money;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consolidated read-only figures for administrative consultation (OBJ-12).
 */
public class AdministrativeSummary {

    private long totalUsers;
    private Map<String, Long> usersByRole = new LinkedHashMap<>();
    private Map<String, Long> ordersByStatus = new LinkedHashMap<>();
    private Money paidSales;
    private long publishedProducts;
    private long pendingShipments;
    private long pendingRefunds;
    private long damagedInventoryRecords;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }

    public void setUsersByRole(Map<String, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public Money getPaidSales() {
        return paidSales;
    }

    public void setPaidSales(Money paidSales) {
        this.paidSales = paidSales;
    }

    public long getPublishedProducts() {
        return publishedProducts;
    }

    public void setPublishedProducts(long publishedProducts) {
        this.publishedProducts = publishedProducts;
    }

    public long getPendingShipments() {
        return pendingShipments;
    }

    public void setPendingShipments(long pendingShipments) {
        this.pendingShipments = pendingShipments;
    }

    public long getPendingRefunds() {
        return pendingRefunds;
    }

    public void setPendingRefunds(long pendingRefunds) {
        this.pendingRefunds = pendingRefunds;
    }

    public long getDamagedInventoryRecords() {
        return damagedInventoryRecords;
    }

    public void setDamagedInventoryRecords(long damagedInventoryRecords) {
        this.damagedInventoryRecords = damagedInventoryRecords;
    }

}
