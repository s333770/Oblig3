package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.tree.TreeNode;
import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
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
        public String toString() {
            return "" + verdi;
        }

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
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ikke lov med null verdier");
        Node<T> p = rot, q = null; //p = root, Node q er en null verdi
        int cmp = 0;

        while (p != null) {
            q = p;
            cmp = comp.compare(verdi, p.verdi);
            p = cmp < 0 ? p.venstre : p.høyre; // Hvis cmp er mindre enn null er p lik pvenstre, ellers høyre
        }
        p = new Node<>(verdi, q); // q er forelderen til p
        if (q == null) {
            rot = p;
        } else if (cmp < 0) {
            q.venstre = p;
        } else {
            q.høyre = p;
        }
        antall++;
        endringer++;
        return true;
    }

    @Override
    public boolean inneholder(T verdi) {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null) {
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
            int cmp = comp.compare(verdi, p.verdi);      // sammenligner
            if (cmp < 0) {
                q = p;
                p = p.venstre;
            }      // går til venstre
            else if (cmp > 0) {
                q = p;
                p = p.høyre;
            }   // går til høyre
            else break;    // den søkte verdien ligger i p
        }
        if (p == null) return false;   // finner ikke verdi

        if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
        {
            Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
            if (p == rot) rot = b;
            else if (p == q.venstre){
                q.venstre = b;
                if(b!=null){
                    b.forelder=q;
                }
            }
            else {
                q.høyre = b;
                if(b!= null) {
                    b.forelder = q;
                }
            }
        } else  // Tilfelle 3)
        {
            Node<T> s = p, r = p.høyre;   // finner neste i inorden
            while (r.venstre != null) {
                s = r;    // s er forelder til r
                r = r.venstre;
            }

            p.verdi = r.verdi;   // kopierer verdien i r til p

            if (s != p) {
                s.venstre = r.høyre;
                if (r.høyre != null) {
                    r.forelder.høyre = s;
                } else {
                  //  s.høyre = r.høyre;
                    if (r.høyre != null) {
                        r.forelder.høyre = s;

                    }
                }
            }
        }

        antall--;   // det er nå én node mindre i treet
            endringer++;
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
        endringer=0;
        antall=0;
    }
  private static <T> Node<T> førsteInorden(Node<T> p)
  {
      if(p==null){
          return p;
      }
    while (p.venstre != null) p = p.venstre;
    return p;
  }

  private static <T> Node<T> nesteInorden(Node<T> p)
  {
      if(p==null){
          return null;
      }
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
      if(p==null){
          s.append("[]");
          return s.toString();
      }
    s.append('[');
    while(p!=null){
        s.append(p.verdi);
        p=nesteInorden(p);
        s.append(", ");
        }


    s.setLength(s.length()-2);// For å fjerne komma
      s.append(']');
    return s.toString();
  }

public String omvendtString()  // iterativ inorden
{
    Stakk<Node<T>> stakk = new TabellStakk<>();
    Node<T> p = rot;   // starter i roten og går til venstre
    StringBuilder s = new StringBuilder();
    s.append("[");
    if(p==null){
        s.append("]");
        return s.toString();
    }
    for ( ; p.høyre != null; p = p.høyre)
    {
        stakk.leggInn(p);
    }

    while (true)
    {
        s.append(p.verdi);
        s.append(", ");
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
    s.setLength(s.length() - 2);
    s.append("]");

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
    StringBuilder s=new StringBuilder();

    if(p==null){
        s.append("[]");
        return s.toString();
    }
    stakk.push(p);
    while(!stakk.isEmpty()){
        p=stakk.remove(0);


    if(p.høyre!=null){
        stakk.add(p.høyre);
    }
    if(p.venstre!=null){
        stakk.add(p.venstre);
    }
    }
    T verdi=p.verdi;
    s.append("[");
    Stack<Node> kø=new Stack<Node>();
    kø.add(p);
    while(p.forelder!=null){
        p=p.forelder;
        kø.add(p);
    }
    s.append(kø.pop());
    while(!kø.isEmpty()){
        s.append(", ").append(kø.pop());
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
      if(p==null){
          s.append("]");
          return s.toString();
      }
      if (!tom()){
          bladnodeverdier(rot,s);
      }
      s.setLength(s.length()-2);
      s.append("]");
    return s.toString();
  }
  public String bladnodeverdier(Node<T> p,StringBuilder s){
      if(p==null){
          s.append("[]");
          return s.toString();
      }
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
  public String postString(){
      StringBuilder ut=new StringBuilder();
      Node<T> p=rot;
      if(p==null){
          ut.append("[]");
          return ut.toString();
      }
      Stack<Node<T>> stakk=new Stack<>();
      stakk.push(p);
      Stack<T>utStack=new Stack<>();
      while(!stakk.isEmpty()){
           Node denne=stakk.pop();
           utStack.push((T) denne.verdi);
          if (denne.venstre != null) {
              stakk.push(denne.venstre);
          }

          if (denne.høyre != null) {
              stakk.push(denne.høyre);
          }
      }
      ut.append("[");
      while (!utStack.empty()) {
          ut.append(utStack.pop());
        ut.append(", ");
      }

      ut.setLength(ut.length()-2);
      ut.append("]");
    return ut.toString();
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
    private Stakk<Node<T>> s=new TabellStakk<>();


    private BladnodeIterator()  // konstruktør
    {
       p=forsteVenstre(rot);
    }

    private Node<T> forsteVenstre(Node <T> p) {

        while(true){
            if(p.venstre!=null)
            {
                p=p.venstre;
            }
            else if(p.venstre==null && p.høyre!=null){
                p=p.høyre;
            }
            if(p.venstre==null && p.høyre==null){
                break;
            }
        }
        return p;
    }

        /*
        while(p!=null){
            if(p.venstre ==null &&p.høyre==null){
                break;
            }
            if(p.venstre!=null){
                if(p.høyre!=null){
                    s.leggInn(p.høyre);
                }
                p=p.venstre;
            }
            else{
                p=p.høyre;
            }

        }
        return p;
    }
    */


    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }


    @Override
    public T next() {

        while (true) {
            p = p.forelder;
            if (p.høyre != null) {
                p = p.høyre;
                forsteVenstre(p);
                break;
            } else {
                p = p.forelder;
            }
        }
        return (T) p;
    }




        /*
        T verdi = p.verdi;
        if (!s.tom()) {
            p = forsteVenstre(s.taUt());
            return verdi;
        } else {
            p = null;
        }
        q = p;
        while (hasNext()) {
            p = nesteInorden(p);

            if (p == null) {
                return verdi;
            } else if (p.venstre == null && p.høyre == null) {
                return verdi;
            } else if (p.venstre != null && p.høyre == null) {
                return p.venstre.verdi;
            } else if (p.høyre != null && p.venstre == null) {
                return p.høyre.verdi;
            }


        }
        */


    
    @Override
    public void remove()
    {

         if(q.forelder==null) {
             rot = null;
         }
        else{
            if(q.forelder.venstre==q){
                q.forelder.venstre=null;
            }
            else{
                q.forelder.høyre=null;
            }
        }
        antall--;
        endringer++;
        iteratorendringer++;

    }

  } // BladnodeIterator

  public static void main(String[] args) {
      no.oslomet.cs.algdat.Oblig3.ObligSBinTre<Integer> tre =
              new ObligSBinTre<>(Comparator.naturalOrder());


      int[] a = {5, 2, 8, 1, 4, 6, 9, 3, 7};
      for (int k : a) tre.leggInn(k);

      Iterator<Integer> i = tre.iterator();
      List<Integer> liste = new ArrayList<>();
      /*for (Integer verdi : tre) {
          liste.add(verdi);
      }
      */

      System.out.println(tre);




  }
} // ObligSBinTre
