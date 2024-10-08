package Portfolio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Runner {
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        String response;
        LinkedList<Student> studentList = new LinkedList<>();
        // A while true loop isn't the best but I think it works pretty well here
        // I don't like doing manual data entry, it makes testing the program very
        // annoying, I prefer files
        // Actually maybe there's a tool that can automatically do console entry for me.
        // I'll have to look into that for next class
        while (true) {
            System.out.println("Add another student? (Y/N)");
            response = in.nextLine().toUpperCase();
            if (response.equals("Y")) {
                studentList.add(newStudent());
            } else {
                break;
            }
        }
        // I feel proud whenever I use lamda expressions like that
        studentList.sort((a, b) -> (a.compareTo(b)));
        // Print out all contents now they are sorted
        // Also at the same time put them into a file
        try {
            File file = new File("Portfolio/student.txt");
            FileWriter writer = new FileWriter(file);
            for (Student s : studentList) {
                System.out.println(s);
                writer.write(s.toString());
            }
            writer.close();
            System.out.println("File complete");
        } catch (IOException e) {
            System.out.println(e);
        }
        // Now I'm not sure what validating numeric data for the GPA means but google says it checking if it meets input paramaters
        // So I'll ask for a minimum gpa and give the students that are below that and above that
        System.out.println("What is the minimum GPA? Splits into >= and <");
        double req = in.nextDouble();
        LinkedList<Student> goodStudentList = new LinkedList<>();
        LinkedList<Student> badStudentList = new LinkedList<>();
        for (Student student : studentList) {
            // I've learned a limitation of the mini if statement I like to use, the value it returns has to be assigned to something/ used
            // If I removed the notImportant variable it's not recognized as an if statement.
            boolean notImportant = student.gpa >= req ? goodStudentList.add(student) : badStudentList.add(student);
        }
        System.out.println("Good Students:");
        for (Student student : goodStudentList) {
            System.out.println(student);
        }
        System.out.println("Bad Students:");
        for (Student student : badStudentList) {
            System.out.println(student);
        }
        
        
    }

    public static Student newStudent() {
        System.out.print("Name?");
        String name = in.nextLine();
        System.out.print("Address?");
        String address = in.nextLine();
        System.out.print("GPA?");
        double gpa = in.nextDouble();
        in.nextLine();
        return new Student(name, address, gpa);
    }
}
