package system_a;

import java.io.*;
import java.util.*;

/**
 * Filtru care citește toate înregistrările de la portul de intrare,
 * le sortează alfabetic după numele studentului,
 * și apoi le scrie la portul de ieșire.
 */
public class SortFilter extends Filter {

    protected BufferedReader pInput;
    protected BufferedWriter pOutput;
    protected String outputFile;
    protected List<Student> studentList;

    public SortFilter(String sName, BufferedReader pInput, BufferedWriter pOutput, String outputFile) {
        super(sName);
        this.pInput = pInput;
        this.pOutput = pOutput;
        this.outputFile = outputFile;
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
        } else {
            System.out.println("else");
        }

        try (BufferedWriter clearWriter = new BufferedWriter(new FileWriter(outputFile, false))) {
            clearWriter.write("");
        }

        BufferedWriter freshWriter = new BufferedWriter(new FileWriter(outputFile, false));

        studentList.sort(Comparator.comparing(Student::getsName));
        for (Student s : studentList) {
            freshWriter.write(s.getsName() + " " + s.getsProgram());
            freshWriter.newLine();
        }
        freshWriter.flush();
        freshWriter.close();
    }
}