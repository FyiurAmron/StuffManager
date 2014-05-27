package vax.stuffmanager;

import javax.persistence.*;

/**

 @author toor
 */
@MappedSuperclass
abstract public class StuffItem {
  @Id @GeneratedValue
  protected int id;

  public int getId() {
    return id;
  }
}
