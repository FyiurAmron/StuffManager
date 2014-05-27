package vax.stuffmanager;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.List;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

/**

 @author toor
 @param <T>
 */
public class StuffManager<T extends StuffItem> {
  protected final Class<T> t_class;
  protected final String t_class_name, search_query_base;
  protected final JTextField jtf_filter = new JTextField( 80 );
  protected final EntityManagerFactory emf;
  protected final EntityManager em;
  protected final JFrame jf;
  protected List<T> lei;

  private void refresh_list( JList<T> jll ) {
    String query = search_query_base;
    String filter_query = jtf_filter.getText();
    if ( filter_query.length() > 0 )
      query += " WHERE " + filter_query;
    try {
      lei = em.createQuery( query, t_class ).getResultList();
      DefaultListModel<T> dlm = new DefaultListModel<>();
      for( T ei : lei )
        dlm.addElement( ei );
      jll.setModel( dlm );
    } catch (PersistenceException ex) {
      ex.printStackTrace();
    }
  }

  public StuffManager( Class<T> t_class, String db_path ) {
    this.t_class = t_class;
    t_class_name = t_class.getSimpleName();
    search_query_base = "SELECT OBJECT(o) FROM " + t_class_name + " o";

    emf = Persistence.createEntityManagerFactory( db_path );
    em = emf.createEntityManager();

    jf = new JFrame( "StuffManager" );
    jf.getContentPane().setLayout( new FlowLayout() );
    jf.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent e ) {
        em.close();
        emf.close();
        System.exit( 0 );
      }
    } );
    ReflectorPanel<T> orp = new ReflectorPanel<>( t_class );
    try {
      orp.setReflectedObject( em, t_class.newInstance() );
    } catch (InstantiationException | IllegalAccessException ex) {
      ex.printStackTrace();
    }

    JList<T> jll = new JList<>();
    refresh_list( jll );
    jll.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    jll.addListSelectionListener( (ListSelectionEvent e) -> {
      T ei = jll.getSelectedValue();
      if ( ei != null )
        orp.setReflectedObject( em, ei );
    } );
    JPanel left_panel = new JPanel();
    left_panel.setLayout( new BoxLayout( left_panel, BoxLayout.Y_AXIS ) );
    left_panel.add( new JScrollPane( jll ) );
    JPanel button_panel = new JPanel();
    JButton jb_add = new JButton( "add" ),
            jb_remove = new JButton( "remove" ),
            jb_update = new JButton( "update" ),
            jb_update_all = new JButton( "update all" ),
            jb_new = new JButton( "new" ),
            jb_filter = new JButton( "filter" );
    jb_add.addActionListener( (ActionEvent e) -> {
      T ei = orp.getObject();
      em.detach( ei );
      ei.id = 0;
      em.getTransaction().begin();
      em.persist( orp.getObject() );
      em.getTransaction().commit();
      refresh_list( jll );
    } );
    jb_remove.addActionListener( (ActionEvent e) -> {
      T target1 = jll.getSelectedValue(), target2 = em.find( t_class, target1.getId() );
      em.getTransaction().begin();
      em.remove( target2 );
      em.getTransaction().commit();
      refresh_list( jll );
    } );
    jb_update.addActionListener( (ActionEvent e) -> {
      em.getTransaction().begin();
      em.persist( orp.getObject() );
      em.getTransaction().commit();
      refresh_list( jll );
    } );
    jb_update_all.addActionListener( (ActionEvent e) -> {
      em.getTransaction().begin();
      for( T ei : lei )
        em.persist( ei );
      em.getTransaction().commit();
      refresh_list( jll );
    } );
    jb_new.addActionListener( (ActionEvent e) -> {
      try {
        orp.setReflectedObject( em, t_class.newInstance() );
        jll.clearSelection();
      } catch (InstantiationException | IllegalAccessException ex) {
        ex.printStackTrace();
      }
    } );
    jb_filter.addActionListener( (ActionEvent e) -> {
      refresh_list( jll );
    } );
    button_panel.add( jb_add );
    button_panel.add( jb_remove );
    button_panel.add( jb_update );
    button_panel.add( jb_update_all );
    button_panel.add( jb_new );
    button_panel.add( jb_filter );
    left_panel.add( button_panel );
    left_panel.add( new JLabel( search_query_base + " WHERE " ) );
    left_panel.add( jtf_filter );
    jtf_filter.addActionListener( (ActionEvent e) -> {
      refresh_list( jll );
    } );
    jf.add( left_panel );
    jf.add( orp.getPanel() );
    jf.pack();

  }

  public JFrame getFrame() {
    return jf;
  }
}
