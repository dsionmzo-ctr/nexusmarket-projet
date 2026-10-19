package application.domain.ports.out;

import java.math.BigDecimal;

/**
 * Output port: externally configurable business parameters.
 */
public interface BusinessConfigurationPort {

    /** Sales tax rate applied to invoices (e.g. 0.19). */
    BigDecimal getTaxRate();

    /** Days after the delivery during which a return can be requested. */
    Integer getReturnWindowDays();
}
