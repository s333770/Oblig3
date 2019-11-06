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
        Node<T> p = rot;
        Node<T> q = null; //p = root, Node q er en null verdi
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
    //Skriv om denne
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
            else if (p == q.venstre) {
                q.venstre = b;
                if (b != null) {
                    b.forelder = q;
                }
            } else {
                q.høyre = b;
                if (b != null) {
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

        antall--;
        endringer++; //Lagt til endringer da fjerning også er en endring
        return true;
    }


    public int fjernAlle(T verdi) {
        if (verdi == null) throw new
                IllegalArgumentException("verdi er null!");

        Node<T> p = rot;   // hjelpepeker
        Node<T> q = null;  // forelder til p
        Node<T> r = null;  // neste i inorden mhp. verdi
        Node<T> s = null;  // forelder til r

        Stakk<Node<T>> stakk = new TabellStakk<>();

        while (p != null)     // leter etter verdi
        {
            int cmp = comp.compare(verdi, p.verdi);  // sammenligner

            if (cmp < 0) // skal til venstre
            {
                s = r;
                r = q = p;
                p = p.venstre;
            } else {
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
        int verdiAntall = stakk.antall() / 2;

        if (verdiAntall == 0) return 0;

        while (stakk.antall() > 2) {
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
        if (p.venstre == null || p.høyre == null) {
            Node<T> x = p.høyre == null ? p.venstre : p.høyre;
            if (p == rot) rot = x;
            else if (p == q.venstre) q.venstre = x;
            else q.høyre = x;
        } else  // p har to barn
        {
            p.verdi = r.verdi;   // kopierer fra den neste i inorden
            if (r == p.høyre) p.høyre = r.høyre;
            else s.venstre = r.høyre;
        }

        antall -= verdiAntall;

        return verdiAntall;
    }

    @Override
    public int antall() {
        return antall;
    }

    public int antall(T verdi) {

        Node<T> p = rot; //Begynner i rot
        int counter = 0;
        while (p != null) //Så lenge p ikke er null
        {
            int cmp = comp.compare(verdi, p.verdi); // Legger inn Comparator

            if (cmp < 0) {// Går til venstre
                p = p.venstre;
            } else if (cmp > 0) { //Går til høyre
                p = p.høyre;
            } else if (cmp == 0) { //Går enda til høyre, da like verdier ligger der
                counter++;
                p = p.høyre;
            }
        }
        return counter;
    }

    @Override
    public boolean tom() {
        return antall == 0;
    }

    @Override
    public void nullstill() {
        if (!tom()) nullstill(rot);
        rot = null;
        antall = 0;
    }

    private void nullstill(Node<T> p) {
        if (p.venstre != null) {
            nullstill(p.venstre);
            p.venstre = null;
        }
        if (p.høyre != null) {
            nullstill(p.høyre);
            p.høyre = null;
        }
        p.verdi = null;
        endringer++; // Oppdater endringer også her
    }

    private static <T> Node<T> førsteInorden(Node<T> p) {
        if (p == null) {
            return p;
        }
        while (p.venstre != null) p = p.venstre;
        return p;
    }

    private static <T> Node<T> nesteInorden(Node<T> p) {
        if (p == null) {
            return null;
        }
        if (p.høyre != null)  // p har høyre barn
        {
            return førsteInorden(p.høyre);
        } else  // må gå oppover i treet
        {
            while (p.forelder != null && p.forelder.høyre == p) {
                p = p.forelder;
            }
            return p.forelder;
        }
    }

    private static <T> void toString(Node<T> p, StringBuilder s) {
        if (p.venstre != null) {
            toString(p.venstre, s);
            s.append(',').append(' ');
        }

        s.append(p.verdi);

        if (p.høyre != null) {
            s.append(',').append(' ');
            toString(p.høyre, s);
        }
    }


    public String toString() {
        Node<T> p = førsteInorden(rot);
        StringBuilder s = new StringBuilder();
        if (p == null) {
            s.append("[]");
            return s.toString();
        }
        s.append('[');
        while (p != null) {
            s.append(p.verdi);
            p = nesteInorden(p);
            s.append(", ");
        }


        s.setLength(s.length() - 2);// For å fjerne komma
        s.append(']');
        return s.toString();
    }

    public String omvendtString() {
        if (tom()) return "[]";
        StringBuilder s = new StringBuilder();   // StringBuilder
        s.append('[');                           // starter med [

        Node<T> p = rot;
        while (p.høyre != null) p = p.høyre;
        s.append(p.verdi);

        while (true) {
            if (p.venstre != null) {
                p = p.venstre;
                while (p.høyre != null) p = p.høyre;
            } else {
                while (p.forelder != null && p.forelder.venstre == p) {
                    p = p.forelder;
                }
                p = p.forelder;
            }
            if (p == null) break;
            s.append(',').append(' ').append(p.verdi);
        }
        s.append(']');

        return s.toString();
    }


    public String høyreGren() {
        //Fikk ikke til å bruke vedlagte Kø og Lister, brukte derfor java sine egne
        List<T> ut = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        if (rot == null) {
            return ut.toString(); //Returnerer null
        }
        queue.offer(rot);
        while (queue.size() != 0) {
            int lengdeKø = queue.size();
            for (int i = 0; i < lengdeKø; i++) {
                Node denne = queue.poll();
                if (i == 0) { //Tar alltid kun den første verdien fra høyre ut av køen
                    ut.add((T) denne.verdi);
                }
                if (denne.høyre != null) {
                    queue.offer(denne.høyre);
                }
                if (denne.venstre != null) {
                    queue.offer(denne.venstre);
                }
            }
        }
        return ut.toString();
    }

    public String lengstGren() {
        //Problemer med vedlagt stakk, brukte derfor Java sin
        Stack<Node> stakk = new Stack<Node>();
        Node<T> p = rot;
        StringBuilder s = new StringBuilder();
        if (p == null) {
            s.append("[]");
            return s.toString();
        }
        //Her begynner logikken der vi ved hjelp av en stack finner noden som er lengst ned
        stakk.push(p);
        while (!stakk.isEmpty()) {//Legger til lengste verdi på veien
            p = stakk.remove(0);
            if (p.høyre != null) {
                stakk.add(p.høyre);
            }
            if (p.venstre != null) {
                stakk.add(p.venstre);
            }
        }
        //p er noden som ligger lengst ned
        s.append("[");
        Stack<Node> stakkUt = new Stack<Node>();
        stakkUt.add(p);
        while (p.forelder != null) {
            p = p.forelder; // Legger foreldrene inn til en stack ut
            stakkUt.add(p);
        }
        s.append(stakkUt.pop());
        while (!stakkUt.isEmpty()) {
            s.append(", ").append(stakkUt.pop()); // Tar ut verdiene
        }
        s.append("]");
        return s.toString(); // Ikke veldig effektiv kode, mulig man kan effektivisere de to stackene, men den fungerer
    }

    public String[] grener() {
        Liste<String> liste = new TabellListe<>();
        StringBuilder s = new StringBuilder("[");
        if (!tom()) grener(rot, liste, s);

        String[] grener =
                new String[liste.antall()];           // oppretter tabell

        int i = 0;
        for (String gren : liste)
            grener[i++] = gren;                   // fra liste til tabell

        return grener;                          // returnerer tabellen
    }

    private void grener(Node<T> p, Liste<String> liste, StringBuilder s) {
        T verdi = p.verdi;
        int k = verdi.toString().length(); // lengden på verdi

        if (p.høyre == null && p.venstre == null)  // bladnode
        {
            liste.leggInn(s.append(verdi).append(']').toString());

            // må fjerne det som ble lagt inn sist - dvs. k + 1 tegn
            s.delete(s.length() - k - 1, s.length());
        } else {
            s.append(p.verdi).append(',').append(' ');  // legger inn k + 2 tegn
            if (p.venstre != null) grener(p.venstre, liste, s);
            if (p.høyre != null) grener(p.høyre, liste, s);
            s.delete(s.length() - k - 2, s.length());   // fjerner k + 2 tegn
        }
    }

    public String bladnodeverdier() {
        Node<T> p = rot;
        StringBuilder s = new StringBuilder("[");
        if (p == null) {
            s.append("]");
            return s.toString();
        }
        if (!tom()) {
            bladnodeverdier(rot, s);
        }
        s.setLength(s.length() - 2);
        s.append("]");
        return s.toString();
    }

    public String bladnodeverdier(Node<T> p, StringBuilder s) {
        if (p == null) {
            s.append("[]");
            return s.toString();
        }
        if (p.venstre == null && p.høyre == null) {
            s.append(p.verdi);
            s.append(", ");
        }
        if (p.venstre != null) {
            bladnodeverdier(p.venstre, s);
        }
        if (p.høyre != null) {
            bladnodeverdier(p.høyre, s);
        }
        return s.toString();
    }


    public String postString() {
        StringBuilder ut = new StringBuilder();
        Node<T> p = rot;
        if (p == null) {
            ut.append("[]");
            return ut.toString();
        }
        Stack<Node<T>> stakk = new Stack<>();
        stakk.push(p);
        Stack<T> utStack = new Stack<>();
        while (!stakk.isEmpty()) {
            Node denne = stakk.pop();
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

        ut.setLength(ut.length() - 2);
        ut.append("]");
        return ut.toString();
    }
    /*Forsøk på rekursivløsing av nesteblad, fungerte ikke
    private static <T> Node nesteBlad(Node <T>p){
        Node q;
        if (p==null){
            return null;
        }
        if(p.venstre==null && p.høyre==null){
         q=p;
         p=p.forelder;
            return q;
        }
        p=p.forelder;
        if(p.venstre!=null){
        nesteBlad(p.venstre);
        }
        if(p.høyre!=null){
        nesteBlad(p.høyre);
        }
        return p.forelder;

    }
    */




      //Lager en hjelpemetode til traversering i next(), omtrent lik som førsteInorden, renamer for ryddighet
      private static <T> Node nesteBlad(Node<T> p) { //Samme metode som Inorden, men her brukes den for å finne neste blad
          //Måtte bruke denne funksjonen igjen, da jeg hadde problemer med p implementere en stack og rekursiv metode
          if (p == null) {
              return null;
          }
          if (p.høyre != null)  // p har høyre barn
          {
             return førsteInorden(p.høyre); //Henter så lengst til venstre barn
          } else  // må gå oppover i treet
          {
              while (p.forelder != null && p.forelder.høyre == p) {

                  p = p.forelder;
              }
              return p.forelder;
          }
      }


  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T> {
      private Node<T> p = rot, q = null;
      private boolean removeOK = false;
      private int iteratorendringer = endringer;
      private Stakk<Node<T>> s = new TabellStakk<>();


      private BladnodeIterator()  // konstruktør
      {
          p = forsteVenstre(rot);
      }

      private Node<T> forsteVenstre(Node<T> p) {
          if (p == null) {
              return null;
          }
          while (true) {
              if (p.venstre != null) {
                  p = p.venstre;
              } else if (p.venstre == null && p.høyre != null) {
                  p = p.høyre;
              }
              if (p.venstre == null && p.høyre == null) {
                  break;
              }
          }
          return p;
      }


      @Override
      public boolean hasNext() {
          return p != null;  // Denne skal ikke endres!
      }


      @Override
      public T next() {
          if (!hasNext()) {
              throw new NoSuchElementException("Ingen flere bladnoder");
          }
          if(endringer!=iteratorendringer){
              throw new ConcurrentModificationException("Antallet endringer stemmer ikke ");
          }
          q=p;// Setter q til forelder til p
          T tempVariabel = p.verdi;// Lager en tempvariabel som skal hjelpe oss med riktige verdier under traversering
          removeOK=true; // Kan fjerne verdier
          while (hasNext()) {
              p = nesteBlad(p); // Bruker neste Inorder for å traversere treet og finne
                                    //neste verdi av p
              if (p == null) {
                  return tempVariabel;
              }
              else if (p.venstre == null && p.høyre == null) {
                  return tempVariabel;
              }
          }
          return tempVariabel;
      }
          @Override
          public void remove ()
          {
              if(!removeOK){
                  throw new IllegalStateException("Remove flagg er ikke satt, slett er ikke lov ");
              }
              removeOK=false; // Setter flagget og venter på ny tillatelse fra iteratoren
              if (q.forelder == null) { //
                  rot = null;
              } else {
                  if (q.forelder.venstre == q) {
                      q.forelder.venstre = null;
                  } else {
                      q.forelder.høyre = null;
                  }
              }
              antall--;
              endringer++;
              iteratorendringer++;

          }

      }
  public static void main(String[] args) {

  }
}
