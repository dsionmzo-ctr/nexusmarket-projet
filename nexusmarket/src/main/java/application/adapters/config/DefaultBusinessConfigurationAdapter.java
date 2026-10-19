package application.adapters.config;

import application.domain.ports.out.BusinessConfigurationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Business parameters read from application properties (nexusmarket.business.*). */
@Component
public class DefaultBusinessConfigurationAdapter implements BusinessConfigurationPort {

    private final BigDecimal taxRate;
    private final int returnWindowDays;

    public DefaultBusinessConfigurationAdapter(
            @Value("${nexusmarket.business.tax-rate:0.19}") BigDecimal taxRate,
            @Value("${nexusmarket.business.return-window-days:30}") int returnWindowDays) {
        if (taxRate.signum() < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("nexusmarket.business.tax-rate must be between 0 and 1.");
        }
        if (returnWindowDays < 0) {
            throw new IllegalStateException("nexusmarket.business.return-window-days must not be negative.");
        }
        this.taxRate = taxRate;
        this.returnWindowDays = returnWindowDays;
    }

    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    @Override
    public Integer getReturnWindowDays() {
        return returnWindowDays;
    }
}
