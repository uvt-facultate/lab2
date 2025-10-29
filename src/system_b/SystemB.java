package system_b;

import java.io.*;

public class SystemB {

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
            System.out.println("Utilizare corecta: java SystemB <fisier_de_intrare> <fisier_de_iesire>");
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

            BufferedReader roleInputFileSync    = new BufferedReader(new FileReader(args[0]));

            BufferedWriter roleIACDSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleIACDSync   = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonIACDSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonIACDSync   = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleIACDAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleIACDAcceptedSync   = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonIACDAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonIACDAcceptedSync   = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonIACDAccepted13222Source = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonIACDAccepted13222Sync   = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleOutputFileSource = new BufferedWriter(new FileWriter(args[1]));

            // Crearea filtrelor (transferul rolurilor ca parametrii, pentru a fi legati 
            // la porturile fiecarui filtru).
            System.out.println("Controller: Creare componente ...");
            SplitFilter filterSplitIACD
                = new SplitFilter("IACD", roleInputFileSync, roleIACDSource, roleNonIACDSource, "IACD");
            CourseFilter filterScreen13456and12333IACD
                = new CourseFilter("IACD-13456-12333", roleIACDSync, roleIACDAcceptedSource, new int[]{13456, 12333});
			CourseFilter filterScreen13456and12333nonIACD
                = new CourseFilter("non-IACD-13456-12333", roleNonIACDSync, roleNonIACDAcceptedSource, new int[]{13456, 12333});
            CourseFilter filterScreen13222nonIACD
                    = new CourseFilter("non-IACD-13222", roleNonIACDAcceptedSync, roleNonIACDAccepted13222Source, new int[]{13222});
            MergeFilter filterMergeAccepted
                = new MergeFilter("Accepted", roleIACDAcceptedSync, roleNonIACDAccepted13222Sync, roleOutputFileSource);

            // _____________________________________________________________________
            // Executarea sistemului
            // _____________________________________________________________________

            // Start all filters.
            System.out.println("Controller: Pornire filtre ...");
            filterSplitIACD.start();
            filterScreen13456and12333IACD.start();
            filterScreen13456and12333nonIACD.start();
            filterScreen13222nonIACD.start();
            filterMergeAccepted.start();

            // Asteapta pana la terminarea datelor de pe lanturile conductelor si filtrelor. 
            // Ordinea de verificare, de la intrare la iesire, este importanta pentru a evita problemele de concurenta.
            // Analizati ce s-ar intampla daca lantul pipe-and-filter ar fi circular.
            while (roleInputFileSync.ready() || filterSplitIACD.busy()
                    || roleIACDSync   .ready() || filterScreen13456and12333IACD.busy() || roleIACDAcceptedSync.ready()
                    || roleNonIACDSync.ready() || filterScreen13456and12333nonIACD.busy() || roleNonIACDAcceptedSync.ready()
                    || roleNonIACDAccepted13222Sync.ready() || filterMergeAccepted.busy()) {
                // Afiseaza un semnal de feedback signal si transfera controlul pentru planifcarea altui fir de executie.
                System.out.print('.');
                Thread.yield();
            }

            // _____________________________________________________________________
            // Curatarea sistemului
            // _____________________________________________________________________

            // Distrugerea tuturor filtrelor.
            System.out.println("Controller: Distrugerea tuturor componentelor ...");
            filterSplitIACD.interrupt();
            filterScreen13456and12333IACD.interrupt();
            filterScreen13456and12333nonIACD.interrupt();
            filterScreen13222nonIACD.interrupt();
            filterMergeAccepted.interrupt();

            // Verificarea faptului ca filtrele sunt distruse.
            while (filterSplitIACD.isAlive() == false || filterScreen13456and12333IACD.isAlive() == false
                    || filterScreen13456and12333nonIACD.isAlive() == false || filterScreen13222nonIACD.isAlive() == false
                    || filterMergeAccepted.isAlive() == false) {
                // Afiseaza un semnal de feedback si transfera controlul planificatorului de fire de execuitie.
                System.out.print('.');
                Thread.yield();
            }

            // Distrugerea tuturor conductelor.
            System.out.println("Controller: Distrugerea tuturor conectorilor ...");
            roleInputFileSync.close();
            roleOutputFileSource.close();
            roleIACDSource.close();
            roleIACDSync.close();
            roleNonIACDSource.close();
            roleNonIACDSync.close();
            roleIACDAcceptedSource.close();
            roleIACDAcceptedSync.close();
            roleNonIACDAcceptedSource.close();
            roleNonIACDAcceptedSync.close();
            roleNonIACDAccepted13222Source.close();
            roleNonIACDAccepted13222Sync.close();
        }
        catch (Exception e) {
            // Afisarea de informatii pentru debugging.
            System.out.println("Exceptie aparuta in SystemMain.");
            e.printStackTrace();
            System.exit(1);
        }

        // Final!
        System.out.println("Controller: Final!");
    }
}
