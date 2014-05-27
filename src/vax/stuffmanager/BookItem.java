package vax.stuffmanager;

import java.io.Serializable;
import javax.persistence.Entity;

/**

 @author toor
 */
@Entity
public class BookItem extends StuffItem implements Serializable {
  @DBCombo
  protected String cover = "", theme = "";
  //
  protected String title = "", author = "", publish_date = "";
  protected int count, pages;
  protected String notes = "";

  @Override
  public String toString() {
    return "[" + cover + "] [" + theme + "] (" + author + ") " + title + " [" + publish_date + "] " + count + " pcs";
  }
}
