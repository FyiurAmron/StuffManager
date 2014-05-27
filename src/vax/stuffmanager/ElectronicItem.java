package vax.stuffmanager;

import java.io.Serializable;
import javax.persistence.*;

/**

 @author toor
 */
@Entity
public class ElectronicItem extends StuffItem implements Serializable {
  @DBCombo
  protected String vendor = "", type = "", packaging = "";
  protected String name = "", subname = "";
  protected String subtype = "";

  protected int count;
  protected double voltageLow, voltageHigh, maxCurrent, maxPower;
  protected String notes = "";

  protected String str( String s ) {
    return s == null ? "" : s + " ";
  }

  protected String num( double d, String caption ) {
    return d == 0 ? "" : caption + ": " + d + " ";
  }

  @Override
  public String toString() {
    return "(" + count + " pcs) " + str( vendor ) + str( name ) + str( subname ) + str( type ) + str( subtype )
            + num( voltageLow, "Vl" ) + num( voltageHigh, "Vh" ) + num( maxCurrent, "Imax" ) + num( maxPower, "Pmax" );
  }

}
