package com.example.jobtracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {


    public static String filePath = "Software Developer.search";

    public static void openFile() {
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", filePath);
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getFileLines() {

        Profiler p = new Profiler("Read file");
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        p.printTimeMill();
        return lines;
    }
}



