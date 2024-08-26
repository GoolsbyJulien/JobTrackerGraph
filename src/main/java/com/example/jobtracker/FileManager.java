package com.example.jobtracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileManager {
    public static List<String> getFileLines() {

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("Software Developer.search"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }
}



