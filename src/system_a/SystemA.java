package system_a;

import java.io.*;

public class SystemA {

    /**
     * Creaza componentele si porneste sistemul.
     * Sunt asteptati doi parametrii de intrare:
     * primul: numele fisierului de intrare ce contine inregistrarile corespunzatoare studentilor candidati,
     * al doilea: numele fisierului de iesire ca va contine inregistrarile studentilor acceptati.
     *
     * @param args array cu parametrii de intrare
     */
    public static void main(String[] args) {
        // Verificarea numarului parametrilor de intrare.
        if (args.length != 2) {
            System.out.println("Numar incorect de parametri");
            System.out.println("Utilizare corecta: java SystemMain <fisier_de_intrare> <fisier_de_iesire>");
            System.exit(1);
        }

        // Verificarea existentei fisierului de intrare.
        if (!new File(args[0]).exists()) {
            System.out.println("Could not find " + args[0]);
            System.exit(1);
        }

        // Vertificarea existentei directorului parinte al fisierului de iesire.
        // Crearea acestuia daca e necesar.
        File parentFile = new File(args[1]).getAbsoluteFile().getParentFile();
        if (!parentFile.exists() && !parentFile.mkdir()) {
            System.out.println("Nu s-a putut crea directorul parinte " + args[1]);
            System.exit(1);
        }

        try {
            // _____________________________________________________________________
            // Crearea si legarea componentelor si conectorilor
            // _____________________________________________________________________

            // Crearea conductelor (de fapt, a rolurilor).
            System.out.println("Controller: Creare conectori (roluri)...");
            PipedWriter objTemp;

            BufferedReader roleInputFileSync = new BufferedReader(new FileReader(args[0]));

            BufferedWriter roleISSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleISSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonISSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonISSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleISAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleISAcceptedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleISRejectedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleISRejectedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonISAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonISAcceptedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonISRejectedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonISRejectedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleMergedAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleMergedAcceptedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleOutputFileSource = new BufferedWriter(new FileWriter(args[1]));
            BufferedWriter roleOutputFileRejectedSource = new BufferedWriter(new FileWriter(args[1].replace(".txt", "-rejected.txt")));

            // Crearea filtrelor (transferul rolurilor ca parametrii, pentru a fi legati
            // la porturile fiecarui filtru).
            System.out.println("Controller: Creare componente ...");
            SplitFilter filterSplitIS
                    = new SplitFilter("IS", roleInputFileSync, roleISSource, roleNonISSource, "IS");
            CourseFilter filterScreen17651
                    = new CourseFilter("17651", roleISSync, roleISAcceptedSource, roleISRejectedSource, 17651);
            CourseFilter filterScreen21701
                    = new CourseFilter("21701", roleNonISSync, roleNonISAcceptedSource, roleNonISRejectedSource, 21701);
            MergeFilter filterMergeAccepted
                    = new MergeFilter("Accepted", roleISAcceptedSync, roleNonISAcceptedSync, roleMergedAcceptedSource);
            MergeFilter filterMergeRejected
                    = new MergeFilter("Rejected", roleISRejectedSync, roleNonISRejectedSync, roleOutputFileRejectedSource);
            SortFilter filterSortAccepted
                    = new SortFilter("Sort", roleMergedAcceptedSync, roleOutputFileSource, args[1]);

            // _____________________________________________________________________
            // Executarea sistemului
            // _____________________________________________________________________

            // Start all filters.
            System.out.println("Controller: Pornire filtre ...");
            filterSplitIS.start();
            filterScreen17651.start();
            filterScreen21701.start();
            filterMergeAccepted.start();
            filterMergeRejected.start();
            filterSortAccepted.start();

            // Asteapta pana la terminarea datelor de pe lanturile conductelor si filtrelor.
            // Ordinea de verificare, de la intrare la iesire, este importanta pentru a evita problemele de concurenta.
            // Analizati ce s-ar intampla daca lantul pipe-and-filter ar fi circular.
            while (roleInputFileSync.ready() || filterSplitIS.busy()
                    || filterScreen17651.busy() || filterScreen21701.busy()
                    || filterMergeAccepted.busy() || filterSortAccepted.busy()
                    || filterMergeRejected.busy()
                    || roleISSync.ready() || roleNonISSync.ready()
                    || roleISAcceptedSync.ready() || roleNonISAcceptedSync.ready()
                    || roleISRejectedSync.ready() || roleNonISRejectedSync.ready()
                    || roleMergedAcceptedSync.ready()) {
                // Afiseaza un semnal de feedback signal si transfera controlul pentru planifcarea altui fir de executie.
                System.out.print('.');
                Thread.yield();
            }

            // _____________________________________________________________________
            // Curatarea sistemului
            // _____________________________________________________________________

            // Distrugerea tuturor filtrelor.
            System.out.println("Controller: Distrugerea tuturor componentelor ...");
            filterSplitIS.interrupt();
            filterScreen17651.interrupt();
            filterScreen21701.interrupt();
            filterMergeAccepted.interrupt();
            filterMergeRejected.interrupt();
            filterSortAccepted.interrupt();

            // Verificarea faptului ca filtrele sunt distruse.
            while (filterSplitIS.isAlive() == false
                    || filterScreen17651.isAlive() == false || filterScreen21701.isAlive() == false
                    || filterMergeAccepted.isAlive() == false || filterMergeRejected.isAlive() == false
                    || filterSortAccepted.isAlive() == false) {
                // Afiseaza un semnal de feedback si transfera controlul planificatorului de fire de execuitie.
                System.out.print('.');
                Thread.yield();
            }

            // Distrugerea tuturor conductelor.
            System.out.println("Controller: Distrugerea tuturor conectorilor ...");
            roleInputFileSync.close();
            roleOutputFileSource.close();
            roleOutputFileRejectedSource.close();
            roleISSource.close();
            roleISSync.close();
            roleNonISSource.close();
            roleNonISSync.close();
            roleISAcceptedSource.close();
            roleISAcceptedSync.close();
            roleNonISAcceptedSource.close();
            roleNonISAcceptedSync.close();
            roleISRejectedSource.close();
            roleISRejectedSync.close();
            roleNonISRejectedSource.close();
            roleNonISRejectedSync.close();

        } catch (Exception e) {
            // Afisarea de informatii pentru debugging.
            System.out.println("Exceptie aparuta in SystemMain.");
            e.printStackTrace();
            System.exit(1);
        }

        // Final!
        System.out.println("Controller: Final!");
    }
}
