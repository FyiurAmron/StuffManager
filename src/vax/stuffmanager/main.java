package vax.stuffmanager;

/**

 @author toor
 */
public class main {
  /**
   @param args the command line arguments
   */
  public static void main( String[] args ) {
    new StuffManager<>( ElectronicItem.class, "$objectdb/db/ElectronicItems.odb" ).getFrame().setVisible( true );
    //new StuffManager<>( BookItem.class, "$objectdb/db/BookItems.odb" ).getFrame().setVisible( true );

  }
}
