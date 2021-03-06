package techcr.utility.entityvalidator.type.notation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import techcr.utility.entityvalidator.type.NumberCriteriaType;

/**
 * Check for number format.
 * specially for double, float.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberFormat {
    String numberFormat();
    NumberCriteriaType lengthCriteria() default NumberCriteriaType.EQAUL;
    String errorDesc() default "Invalid Number Format";
}
