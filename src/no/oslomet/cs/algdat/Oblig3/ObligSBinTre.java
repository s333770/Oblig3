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
  public boolean fjern(T verdi)  // hører til klassen SBinTre
  {
      if (verdi == null) return false;  // treet har ingen nullverdier

      Node<T> p = rot, q = null;   // q skal være forelder til p

      while (p != null)            // leter etter verdi
      {
          int cmp = comp.compare(verdi,p.verdi);      // sammenligner
          if (cmp < 0) {
              q = p; p = p.venstre;
          }      // går til venstre
          else if (cmp > 0) {
              q = p; p = p.høyre;
          }   // går til høyre
          else break;    // den søkte verdien ligger i p
      }
      if (p == null) return false;   // finner ikke verdi

      if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
      {
          Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
          if (p == rot) {
              rot = b;
          }
          else if (p == q.venstre){
              p.verdi=b.verdi;
              q.venstre = p;
              p.venstre=null;
          }
          else{
              q.høyre = b;
          }
      }
      else  // Tilfelle 3)
      {
          Node<T> s = p, r = p.høyre;   // finner neste i inorden
          while (r.venstre != null)
          {
              s = r;    // s er forelder til r
              r = r.venstre;
          }

          p.verdi = r.verdi;   // kopierer verdien i r til p

          if (s != p) {
              s.venstre = r.høyre;
          }
          else {
              s.høyre = r.høyre;
          }
      }

      antall--;   // det er nå én node mindre i treet
      return true;
  }

  public int fjernAlle(T verdi)
  {
    if (verdi == null) throw new
      IllegalArgumentException("verdi er null!");

    Node<T> p = rot;   // hjelpepeker
    Node<T> q = null;  // forelder til p
    Node<T> r = null;  // neste i inorden mhp. verdi
    Node<T> s = null;  // forelder til r

    Stakk<Node<T>> stakk = new TabellStakk<>();

    while (p != null)     // leter etter verdi
    {
      int cmp = comp.compare(verdi,p.verdi);  // sammenligner

      if (cmp < 0) // skal til venstre
      {
        s = r;
        r = q = p;
        p = p.venstre;
      }
      else
      {
        if (cmp == 0)  // verdi ligger i p
        {
          stakk.leggInn(q);  // legger inn forelder til p
          stakk.leggInn(p);  // legger inn p
        }
        // skal videre til høyre
        q = p;
        p = p.høyre;
      }
    }

    // det er lagt inn to noder for hvert treff
    int verdiAntall = stakk.antall()/2;

    if (verdiAntall == 0) return 0;

    while (stakk.antall() > 2)
    {
      p = stakk.taUt();  // p har ikke venstre barn
      q = stakk.taUt();  // forelder til p

      if (p == q.venstre) q.venstre = p.høyre;
      else q.høyre = p.høyre;
    }

    // Har nå fjernet alle duplikatene,
    // men har igjen første forekomst

    p = stakk.taUt();  // p inneholder verdi
    q = stakk.taUt();  // forelder til p

    // Tilfelle 1) og 2), dvs. p har ikke to barn
    if (p.venstre == null || p.høyre == null)
    {
      Node<T> x = p.høyre == null ? p.venstre : p.høyre;
      if (p == rot) rot = x;
      else if (p == q.venstre) q.venstre = x;
      else q.høyre = x;
    }
    else  // p har to barn
    {
      p.verdi = r.verdi;   // kopierer fra den neste i inorden
      if (r == p.høyre) p.høyre = r.høyre;
      else s.venstre = r.høyre;
    }

    antall -= verdiAntall;

    return verdiAntall;
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
      Node<T> p =førsteInorden(rot);
      StringBuilder s = new StringBuilder();
    s.append('[');
    while(p!=null){
        s.append(p.verdi);
        s.append(" ");
            p=nesteInorden(p);
        }
    s.append(']');
    return s.toString();
  }

public String omvendtString()  // iterativ inorden
{
    Stakk<Node<T>> stakk = new TabellStakk<>();
    Node<T> p = rot;   // starter i roten og går til venstre
    StringBuilder s = new StringBuilder();

    for ( ; p.høyre != null; p = p.høyre) stakk.leggInn(p);

    while (true)
    {
        s.append('[');
        s.append(p.verdi);
        s.append("] ,");
        if (p.venstre != null)          // til venstre i høyre subtre
        {
            for (p = p.venstre; p.høyre != null; p = p.høyre)
            {
                stakk.leggInn(p);
            }
        }
        else if (!stakk.tom())
        {
            p = stakk.taUt();   // p.høyre == null, henter fra stakken
        }
        else break;          // stakken er tom - vi er ferdig

    } // while
    return s.toString();
}
  
  public String høyreGren() {
      Node<T> p = rot;
      ArrayList Q = new ArrayList<Node>();
      Q.add(p); // Adder node p
      int nivå = 0, maksNivå = 0;
      while (!Q.isEmpty()) {
          int count = Q.size();
          // store the current size of the Q
          nivå += 1;
          while (count > 0) {
              // pop the first node from the queue
              Node NODE = (Node) Q.get(0);
              Q.remove(0);
              if (maksNivå < nivå) {
                  System.out.print(NODE.verdi + " ");
                  maksNivå = nivå;
              }
              if (NODE.høyre != null) {
                  Q.add(NODE.høyre);
              }
              if (NODE.venstre != null) {
                  Q.add(NODE.venstre);
              }
              count--;
          }
      }
      return "hello";
  }
  
  public String lengstGren()
  {
      Node<T> p = rot;
      path[arrayCounter++] = node.data;

      if (node.leftNode == null && node.rightNode == null) {
          printArray(path);
      }
      else {
          printMain(node.leftNode, path, arrayCounter);
          printMain(node.rightNode, path, arrayCounter);
      }

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

      int [] a = {4,7,2,9,4,10,8,7,4,6,1};
      ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator. naturalOrder ());
      for ( int verdi : a) tre.leggInn(verdi);
      tre.fjernAlle(4);
      //System. out .println(tre.toString()); // 5
     // System. out .println(tre + " " + tre.omvendtString());
    tre.høyreGren();



  }

} // ObligSBinTre
