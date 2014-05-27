package vax.stuffmanager;

import java.util.TreeSet;
import java.lang.reflect.Field;
import javax.persistence.*;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;

/**

 @author toor
 @param <T>
 */
public class ReflectorPanel<T> {
  final protected JPanel my_panel = new JPanel();
  final protected Class<T> t_class;
  final protected String get_set_query_from;
  protected T my_object;

  public ReflectorPanel( Class<T> t_class ) {
    this.t_class = t_class;
    get_set_query_from = " FROM " + t_class.getSimpleName() + " o";
  }

  @SuppressWarnings( "UseOfObsoleteCollectionType" )
  private java.util.Vector<String> get_set( String set_name, EntityManager em ) {
    String query = "SELECT o." + set_name + get_set_query_from;
    try {
      return new java.util.Vector<>( new TreeSet<>( em.createQuery( query, String.class ).getResultList() ) );
    } catch (PersistenceException ex) {
      ex.printStackTrace();
      return new java.util.Vector<>();
    }
  }

  public JPanel getPanel() {
    return my_panel;
  }

  public T getObject() {
    return my_object;
  }

  public void setReflectedObject( EntityManager em, T o ) {
    my_object = o;
    my_panel.removeAll();
    JPanel jp = new JPanel();
    jp.setLayout( new BoxLayout( jp, BoxLayout.Y_AXIS ) );
    try {
      for( Field f : o.getClass().getDeclaredFields() ) {
        if ( f.isAnnotationPresent( Id.class ) )
          continue;
        JPanel item_panel = new JPanel();
        String fname = f.getName();
        item_panel.add( new JLabel( f.getName() ) );
        Class<?> ft = f.getType();
        if ( ft == String.class ) {
          String s = (String) f.get( o );
          if ( f.isAnnotationPresent( DBCombo.class ) ) {
            JComboBox<String> jcb = new JComboBox<>( get_set( fname, em ) );
            jcb.setEditable( true );
            jcb.setSelectedItem( s );
            jcb.addActionListener( (ActionEvent e) -> {
              try {
                f.set( o, jcb.getSelectedItem() );
              } catch (IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
              }
            } );
            item_panel.add( jcb );
          } else {
            JTextField jtf = new JTextField( s );
            jtf.setColumns( 20 );
            //jtf.( null );
            jtf.addCaretListener( (CaretEvent e) -> {
              try {
                f.set( o, jtf.getText() );
              } catch (IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
              }
            } );
            item_panel.add( jtf );
          }
        } else if ( ft == int.class ) {
          SpinnerNumberModel snm = new SpinnerNumberModel( f.getInt( o ), 0, 999999, 1 );
          JSpinner js = new JSpinner( snm );
          js.addChangeListener( (ChangeEvent e) -> {
            try {
              f.set( o, snm.getNumber() );
            } catch (IllegalArgumentException | IllegalAccessException ex) {
              ex.printStackTrace();
            }
          } );
          item_panel.add( js );
        } else if ( ft == double.class ) {
          SpinnerNumberModel snm = new SpinnerNumberModel( f.getDouble( o ), 0.0, 999999.9, 0.5 );
          JSpinner js = new JSpinner( snm );
          js.addChangeListener( (ChangeEvent e) -> {
            try {
              f.set( o, snm.getNumber() );
            } catch (IllegalArgumentException | IllegalAccessException ex) {
              ex.printStackTrace();
            }
          } );
          item_panel.add( js );
        }
        //jp.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
        jp.add( item_panel );
        //System.out.println( f.getName() + " : " + f.get( o ) + " (" + f.getType() + ")" );
      }
    } catch (IllegalAccessException ex) {
      System.out.println( ex );
    }
    my_panel.add( jp );
    my_panel.revalidate();
  }
}
