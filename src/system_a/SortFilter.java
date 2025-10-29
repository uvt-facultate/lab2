package system_a;

import java.io.*;
import java.util.*;

/**
 * Filtru care citește toate înregistrările de la portul de intrare,
 * le sortează alfabetic după numele studentului,
 * și apoi le scrie la portul de ieșire.
 */
public class SortFilter extends Filter {

    /**
     * Portul de intrare.
     **/
    protected BufferedReader pInput;

    /**
     * Primul port de iesire.
     **/
    protected BufferedWriter pOutput;

    /**
     * Al doilea port de iesire.
     **/
    protected BufferedWriter pOutputRejected;

    protected String outputFile;
    protected String outputFileRejected;

    protected List<Student> studentList;

    public SortFilter(String sName, BufferedReader pInput, BufferedWriter pOutput, BufferedWriter pOutputRejected, String outputFile, String outputFileRejected) {
        super(sName);
        this.pInput = pInput;
        this.pOutput = pOutput;
        this.pOutputRejected = pOutputRejected;
        this.outputFile = outputFile;
        this.outputFileRejected = outputFileRejected;
        this.studentList = new ArrayList<>();
    }

    @Override
    protected boolean ready() throws IOException {
        // Rulează o singură dată, când există ceva în input
        return this.pInput.ready();
    }

    @Override
    protected void work() throws IOException {
        String line = this.pInput.readLine();

        if (line != null) {
            studentList.add(new Student(line));
        }

        try (BufferedWriter clearWriter = new BufferedWriter(new FileWriter(outputFile, false))) {
            clearWriter.write("");
        }

        try (BufferedWriter clearWriterRejected = new BufferedWriter(new FileWriter(outputFileRejected, false))) {
            clearWriterRejected.write("");
        }

        BufferedWriter freshWriter = new BufferedWriter(new FileWriter(outputFile, false));
        BufferedWriter freshWriterRejected = new BufferedWriter(new FileWriter(outputFileRejected, false));

        studentList.sort(Comparator.comparing(Student::getsName));
        for (Student s : studentList) {
            if(s.isAccepted != null)
                if (s.isAccepted.contains("accepted")) {
                    freshWriter.write(s.getsName() + " " + s.getsProgram());
                    freshWriter.newLine();
                } else {
                    freshWriterRejected.write(s.getsName() + " " + s.getsProgram());
                    freshWriterRejected.newLine();
                }
        }
        freshWriter.flush();
        freshWriter.close();

        freshWriterRejected.flush();
        freshWriterRejected.close();
    }
}