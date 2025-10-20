package system_a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Un filtru de unificare are doua porturi de intrare si un port de iesire.
 * El transfera la portul de iesire datele disponibile pe oricare din porturile de intrare
 * fara a le modifica.
 * NU exista nici o informatie si nici o premiza legate de ]ncarcarea datelor de pe porturile de intrare.
 * Din acest motiv, cand datele sunt disponibile pe ambele porturi filtrul poate alege
 * oricare port de intrare.
 */
public class MergeFilter extends Filter {

    /**
     * Primul port de intrare.
     **/
    protected BufferedReader pInput1;

    /**
     * Al doilea port de intrare.
     **/
    protected BufferedReader pInput2;

    /**
     * Portul de iesire.
     **/
    protected BufferedWriter pOutput;

    /**
     * Portul de iesire.
     **/
    protected BufferedWriter pOutputRejected;

    /**
     * Construieste un filtru unificator cu numele dat.
     * Porturile sunt impachetate intr-un flux de caratere buffer-at.
     *
     * @param sName   sirul ce reprezinta numele filtrului
     * @param pInput1 primul port de intrare al acestui filtru
     * @param pInput2 al doilea port de intrare al acestui filtru
     * @param pOutput portul de iesire al acestui filtru
     */
    public MergeFilter(String sName, BufferedReader pInput1, BufferedReader pInput2,
                       BufferedWriter pOutput, BufferedWriter pOutputRejected) {
        // Executarea constructorului clasei parinte.
        super(sName);

        // Initializarea porturilor de intrare si de iesire.
        this.pInput1 = pInput1;
        this.pInput2 = pInput2;
        this.pOutput = pOutput;
        this.pOutputRejected = pOutputRejected;
    }

    /**
     * Precizeaza daca datele sunt disponibile pe porturile de intrare.
     *
     * @return <code>true</code> daca si numai daca acest filtru
     * poate citi date de la unul din porturile de intrare.
     * @throws IOException
     */
    protected boolean ready() throws IOException {
        return this.pInput1.ready() || this.pInput2.ready();
    }

    /**
     * Citeste date disponibile la unul din porturile de intrare si
     * scrie date noi la portul de iesire.
     *
     * @throws IOException
     */
    protected void work() throws IOException {
        if (!pInput1.ready() && !pInput2.ready()) {
            // no more input; close output and stop
            pOutput.close();
            this.interrupt();
            return;
        }

        // Selectarea portului de pe care se face citirea.
        BufferedReader pInput = this.pInput1.ready() ? this.pInput1 : this.pInput2;

        // Citirea unei inregistrari de pe portul de intrare selctat.
        Student objStudent = new Student(pInput.readLine());

        // Scrierea inregistrarii la portul de iesire.
        if (objStudent.sSID.contains("accepted")) {
            objStudent.sSID = objStudent.sSID.replace("accepted ", "");
            this.pOutput.write(objStudent.toString());
            this.pOutput.newLine();
            this.pOutput.flush();
        } else {
            objStudent.sSID = objStudent.sSID.replace("rejected ", "");
            this.pOutputRejected.write(objStudent.toString());
            this.pOutputRejected.newLine();
            this.pOutputRejected.flush();
        }
    }
}
