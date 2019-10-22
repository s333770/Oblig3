import no.oslomet.cs.algdat.Oblig3.ObligSBinTre;
import no.oslomet.cs.algdat.Oblig3.Stakk;
import no.oslomet.cs.algdat.Oblig3.TabellStakk;

public class testMetoderFraBoken {
    /*
    public void inorden() // iterativ inorden
    {
        String ut="";
        Stakk<ObligSBinTre.Node<T>> stakk = new TabellStakk<>();
        ObligSBinTre.Node<T> p = rot;   // starter i roten og går til venstre
        for ( ; p.venstre != null; p = p.venstre) stakk.leggInn(p);

        while (true)
        {

            if (p.høyre != null)          // til venstre i høyre subtre
            {
                for (p = p.høyre; p.venstre != null; p = p.venstre)
                {
                    stakk.leggInn(p);
                }
            }
            else if (!stakk.tom())
            {
                p = stakk.taUt();   // p.høyre == null, henter fra stakken
                ut+=p.verdi;
            }
            else{

                break;          // stakken er tom - vi er ferdig
            }
            System.out.println(ut);

        } // while
    }

     */
}
