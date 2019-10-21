package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import javax.swing.tree.TreeNode;
import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v;
      høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }


    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }
  @Override
  public boolean leggInn(T verdi){
    Objects.requireNonNull(verdi,"Ikke lov med null verdier");
    Node<T> p=rot, q=null; //p = root, Node q er en null verdi
    int cmp=0;

    while(p!=null) {
      q = p;
      cmp = comp.compare(verdi, p.verdi);
      p = cmp < 0 ? p.venstre : p.høyre; // Hvis cmp er mindre enn null er p lik pvenstre, ellers høyre
    }
      p=new Node<>(verdi,q); // q er forelderen til p
    if(q==null) {
      rot = p;
    }
    else if (cmp<0) {
      q.venstre = p;
    }
    else {
      q.høyre = p;
    }
    antall++;
    return true;
  }
  
  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }
  
  @Override
  public boolean fjern(T verdi)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public int fjernAlle(T verdi)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public int antall()
  {
    return antall;
  }
  
  public int antall(T verdi){
    int counter=0;
    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);

      if (cmp < 0 ) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
     else if(cmp==0){
       counter++;
       p=p.høyre;
      }
    }
    return counter;
  }
  
  @Override
  public boolean tom()
  {
    return antall == 0;
  }
  
  @Override
  public void nullstill()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  private static <T> Node<T> førsteInorden(Node<T> p)
  {
    while (p.venstre != null) p = p.venstre;
    return p;
  }

  private static <T> Node<T> nesteInorden(Node<T> p)
  {
    if (p.høyre != null)  // p har høyre barn
    {
      return førsteInorden(p.høyre);
    }
    else  // må gå oppover i treet
    {
      while (p.forelder != null && p.forelder.høyre == p)
      {
        p = p.forelder;
      }
      return p.forelder;
    }
  }

  private static <T> void toString(Node<T> p, StringBuilder s)
  {
    if (p.venstre != null)
    {
      toString(p.venstre, s);
      s.append(',').append(' ');
    }

    s.append(p.verdi);

    if (p.høyre != null)
    {
      s.append(',').append(' ');
      toString(p.høyre, s);
    }
  }

  public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append('[');
    if (!tom()) toString(rot, s);
    s.append(']');
    return s.toString();
  }

  public String omvendtString()
  {
    String ut="";
    Stakk<Node<T>> stakk = new TabellStakk<>();
    Node<T> p = rot;   // starter i roten og går til høyre
    for ( ; p.høyre != null; p = p.høyre) stakk.leggInn(p);
    while (true)
    {
      if (p.venstre != null)          // til høyre i venstre subtre
      {
        for (p = p.venstre; p.høyre != null; p = p.høyre)
        {
          stakk.leggInn(p);
        }
      }
      else if (!stakk.tom())
      {

        p = stakk.taUt();   // p.høyre == null, henter fra stakken
        ut+=p.verdi;
      }
      else break;          // stakken er tom - vi er ferdig
    } // while
    return ut;
  }
  
  public String høyreGren()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String lengstGren()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String[] grener()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String bladnodeverdier()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String postString()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    
    private BladnodeIterator()  // konstruktør
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator

  public static void main(String[] args) {

    /*
    ObligSBinTre<String> tre = new ObligSBinTre<>(Comparator.naturalOrder());
    System. out .println(tre.antall()); // Utskrift: 0
    ObligSBinTre<Character>tretre=new ObligSBinTre<>(Comparator.naturalOrder());
    System.out.println(tretre.antall);
    System.out.println("");
    Integer[] a = {4,7,2,9,5,10,8,1,3,6};
    ObligSBinTre<Integer> tretretre = new ObligSBinTre<>(Comparator. naturalOrder ());
    for ( int verdi : a) tretretre.leggInn(verdi);
    System. out .println(tretretre.antall()); // Utskrift: 10
    System.out.println();
*/
    Integer[] ab = {4,7,2,9,4,10,8,7,4,6};
    ObligSBinTre<Integer> tretretretre = new ObligSBinTre<>(Comparator. naturalOrder ());
    for ( int verdi : ab) tretretretre.leggInn(verdi);
    System. out .println(tretretretre.antall()); // Utskrift: 10
    System. out .println(tretretretre.antall(5)); // Utskrift: 0
    System. out .println(tretretretre.antall(4)); // Utskrift: 3
    System. out .println(tretretretre.antall(7)); // Utskrift: 2
    System. out .println(tretretretre.antall(10)); // Utskrift: 1
    System.out.println("");
    int [] a = {4,7,2,9,4,10,8,7,4,6,1};
    ObligSBinTre<Integer> trefire = new ObligSBinTre<>(Comparator. naturalOrder ());
    for ( int verdi : a) trefire.leggInn(verdi);
    System. out .println(trefire);
    System.out.println("");
    int [] aaaa = {4,7,2,9,4,10,8,7,4,6,1};
    ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator. naturalOrder ());
    for ( int verdi : aaaa) tre.leggInn(verdi);
   // System. out .println(tre.omvendtString()); // [10, 9, 8, 7, 7, 6, 4, 4, 4, 2, 1]
  tre.omvendtString();


  }

} // ObligSBinTre
