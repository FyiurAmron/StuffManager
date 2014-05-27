package vax.stuffmanager;

import java.lang.annotation.*;

/**
 Marker annotation for combo'ed DB fields.

 @author toor
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface DBCombo {
}
