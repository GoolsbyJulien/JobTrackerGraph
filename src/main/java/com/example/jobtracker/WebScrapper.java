package com.example.jobtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class WebScrapper {


    public static void main(String[] args) {
        runPythonScript();
    }

    public static void runPythonScript() {
        try {
            String pythonPath = "C:/Users/Julien/Desktop/Database/Project/Job Tracker/jobTracker.py";
            //String pythonExe = "C:/Users/AppData/Local/Continuum/Anaconda/python.exe";
            ProcessBuilder pb = new ProcessBuilder((pythonPath));
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            System.out.println("Running Python starts: " + line);
            int exitCode = p.waitFor();
            System.out.println("Exit Code : " + exitCode);
            line = bfr.readLine();
            System.out.println("First Line: " + line);
            while ((line = bfr.readLine()) != null) {
                System.out.println("Python Output: " + line);


            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
