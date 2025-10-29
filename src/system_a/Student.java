package system_a;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;


/**
 * Clasa reprezinta o inregistrare corespunzatoare unui student.
 * Contine informatii personale despre student si codurile cursurilor absolvite de acesta.
 * Aceasta clasa este construita dintr-un sir orientat pe campuri separate de spatii.
 */
public class Student {

    /**
     * Un sir ce reprezinta ID-ul acestui student.
     */
    protected String sSID;

    /**
     * Un sir ce reprezinta numele studentului.
     */
    private String sName;

    /**
     * Un sir ce reprezinta specializarea la care studentul este inscris.
     */
    private String sProgram;

    /**
     * O lista ce contine codurile cursurilor pe care le-a absolvit acest student.
     * Elementele din lista sunt obiecte de tip <code>Integer</code> reprezentand
     * numerele cursurilor absolvite.
     */
    protected List vCompleted;

    /**
     * Un atribut care retine daca studentul a fost acceptat sau nu la cursul de care
     * suntem interesati
     */
    protected String isAccepted;

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsProgram() {
        return sProgram;
    }

    public void setsProgram(String sProgram) {
        this.sProgram = sProgram;
    }

    /**
     * Construieste o inregistrare student prin parsarea sirului dat.
     * Argumentul <code>sInput</code> este orientat pe campuri avand spatiul ca separator.
     * Primele trei campuri necesare sunt ID student, nume student si specializarea la care este inscris.
     * Urmeaza, daca studentul a absolvit o serie de cursuri, codurile numerice ale acestora.
     * Desi nu e necesar, evitati includerea de caractere"newline" in sirul <code>sInput</code>
     *
     * @param sInput sirul ce reprezinta inregistrarea corespunzatoare studentului
     */
    public Student(String sInput) {
        StringTokenizer objTokenizer = new StringTokenizer(sInput);

        // Preluarea ID-ului si numelui studentului si a specializarii la care este inscris.
        String next = objTokenizer.nextToken();
        if (Objects.equals(next, "accepted") || Objects.equals(next, "rejected")) {
            this.isAccepted = next;
            this.sSID = objTokenizer.nextToken();
        } else {
            this.sSID = next;
        }
        this.sName = objTokenizer.nextToken();
        this.sName = this.sName + " " + objTokenizer.nextToken();
        this.sProgram = objTokenizer.nextToken();

        // Preluarea cursurilor absolvite de student.
        this.vCompleted = new ArrayList();
        while (objTokenizer.hasMoreTokens()) {
            this.vCompleted.add(Integer.valueOf(objTokenizer.nextToken()));
        }
    }

    /**
     * Precizeaza daca studentul a absolvit un curs dat.
     *
     * @param iCourse codul numeric al cursului dat
     * @return <code>true</code> daca si numai daca acest student a absolvit cursul
     * al carui cod este specificat prin intregul <code>iCourse</code>.
     */
    public boolean hasCompleted(int iCourse) {
        return this.vCompleted.contains(iCourse);
    }

    /**
     * Precizeaza daca studentul este inscris la o specializare data.
     *
     * @param sProgram numele specializarii date.
     * @return <code>true</code> daca si numai daca studentul este inscris
     * la specializarea specificata prin sirul <code>sProgram</code>
     */
    public boolean isAffiliatedWith(String sProgram) {
        return this.sProgram.equals(sProgram);
    }

    /**
     * Returneaza o reprezentare sub forma de sir a inregistrarii corespunzatoare acestui student.
     * Sirul rezultat va fi in acelasi format ca si argumentul constructorului acestei clase.
     *
     * @return o reprezentare sub forma de sir a inregistrarii corepunzatoare acestui student
     */
    public String toString() {
        // Crearea sirului ce contine ID si nume student si specializarea la care este inscris.
        String sReturn = this.sSID + " " + this.sName + " " + this.sProgram;

        // Adaugarea codurilor numerice ale cursurilor.
        for (int i = 0; i < this.vCompleted.size(); i++) {
            sReturn = sReturn + " " + this.vCompleted.get(i).toString();
        }

        return sReturn;
    }
}
