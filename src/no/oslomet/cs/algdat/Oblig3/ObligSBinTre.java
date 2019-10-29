package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
      if (!tom()) nullstill(rot);  // nullstiller
      rot = null; antall = 0;      // treet er nå tomt
  }

    private void nullstill(Node<T> p)
    {
        if (p.venstre != null)
        {
            nullstill(p.venstre);      // venstre subtre
            p.venstre = null;          // nuller peker
        }
        if (p.høyre != null)
        {
            nullstill(p.høyre);        // høyre subtre
            p.høyre = null;            // nuller peker
        }
        p.verdi = null;              // nuller verdien
    }
  private static <T> Node<T> førsteInorden(Node<T> p)
  {
    while (p.venstre != null) p = p.venstre;
    return p;
  }
  /*
  private static<T> Node <T> lengsteGren2(Node<T> p){


  }
*/
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

    for ( ; p.høyre != null; p = p.høyre)
    {
        stakk.leggInn(p);
    }

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
      List<T> ut=new ArrayList<>();
      Queue<Node> queue=new LinkedList<>();
      if(rot==null){
          return ut.toString();
      }
      queue.offer(rot);
      while(queue.size()!=0){
          int str=queue.size();
          for(int i=0;i<str;i++){
              Node denne=queue.poll();
              if(i==0){
                  ut.add((T) denne.verdi);
              }
              if(denne.høyre!=null){
                  queue.offer(denne.høyre);
              }
              if(denne.venstre!=null){
                  queue.offer(denne.venstre);
              }
          }

      }
      return ut.toString();
  }

public String lengstGren() {
    Stack<Node> stakk=new Stack<Node>();
    Node<T> p=rot;
    stakk.push(p);
    while(!stakk.isEmpty()){
        System.out.println(stakk.toString());
        p=stakk.remove(0);


    if(p.høyre!=null){
        stakk.add(p.høyre);
    }
    if(p.venstre!=null){
        stakk.add(p.venstre);
    }
    }
    T verdi=p.verdi;
    StringBuilder s=new StringBuilder();
    Queue<Node> kø=new LinkedList<Node>();
    kø.add(p);
    while(p.forelder!=null){
        p=p.forelder;
        kø.add(p);
    }
    s.append(kø.remove());
    while(!kø.isEmpty()){
        s.append(",").append(kø.remove());
    }
    s.append("]");


    return s.toString();
}

  public String[] grener()
  {
      Liste<String> liste = new TabellListe<>();
      StringBuilder s = new StringBuilder("[");
      if (!tom()) grener(rot, liste, s);

      String[] grener = new String[liste.antall()];           // oppretter tabell

      int i = 0;
      for (String gren : liste)
          grener[i++] = gren;                   // fra liste til tabell

      return grener;                          // returnerer tabellen
  }
    private void grener(Node<T> p, Liste<String> liste, StringBuilder s)
    {
        T verdi = p.verdi;
        int k = verdi.toString().length(); // lengden på verdi

        if (p.høyre == null && p.venstre == null)  // bladnode
        {
            liste.leggInn(s.append(verdi).append(']').toString());

            // må fjerne det som ble lagt inn sist - dvs. k + 1 tegn
            s.delete(s.length() - k - 1, s.length());
        }
        else
        {
            s.append(p.verdi).append(',').append(' ');  // legger inn k + 2 tegn
            if (p.venstre != null) grener(p.venstre, liste, s);
            if (p.høyre != null) grener(p.høyre, liste, s);
            s.delete(s.length() - k - 2, s.length());   // fjerner k + 2 tegn
        }
    }
  
  public String bladnodeverdier()
  {
      Node<T> p=rot;
      StringBuilder s = new StringBuilder("[");
      if (!tom()) bladnodeverdier(rot,s);

      s.append("]");
    return s.toString();
  }
  public String bladnodeverdier(Node<T> p,StringBuilder s){
      if (p.venstre == null && p.høyre == null) {
          s.append(p.verdi);
          s.append(", ");
      }
      if(p.venstre!=null){
          bladnodeverdier(p.venstre,s);
      }
      if(p.høyre!=null){
          bladnodeverdier(p.høyre,s);
      }
      return s.toString();
  }
  //Eksperimentell kode, kopiert fra GeeksForGeeks
  public String postString(){
      String StringUt="";
      Node<T> p=rot;
      Stack<Node<T>> stakk=new Stack<>();
      stakk.push(p);
      Stack<T>ut=new Stack<>();
      while(!stakk.isEmpty()){
           Node denne=stakk.pop();
           ut.push((T) denne.verdi);
          if (denne.venstre != null) {
              stakk.push(denne.venstre);
          }

          if (denne.høyre != null) {
              stakk.push(denne.høyre);
          }
      }

      // print post-order traversal
      while (!ut.empty()) {
          StringUt+=ut.pop();

      }
return StringUt.toString();
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
        while (true){
            if
    }
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
      int [] a={2,1,7,6,10,5,4};
      ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator. naturalOrder ());
      for ( int verdi : a) tre.leggInn(verdi);



     // for (String gren : s) System. out .println(gren);
      System. out .println(tre.bladnodeverdier());
      System. out .println(tre.postString());

      */

      //int [] a = {4,7,2,9,4,10,8,7,4,6,1};
      //ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator. naturalOrder ());
      //for ( int verdi : a) tre.leggInn(verdi);

      //System. out .println(tre.toString()); // 5
     // System. out .println(tre + " " + tre.omvendtString());
      //tre.høyreGren();
      //System.out.println(tre.høyreGren());
      //tre.lengstGren();
      //System.out.println(tre.lengstGren());
      ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator. naturalOrder ());
      char [] verdier = "IATBHJCRSOFELKGDMPQN" .toCharArray();
      for ( char c : verdier) tre.leggInn(c);
      tre.postString();
      System.out.println(tre.postString());




  }

} // ObligSBinTre
