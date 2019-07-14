package com.greekk;

import java.io.*;
import java.util.*;

public class FileOperations {

    public static Map<String, String> readConfig(String path) throws IOException {
        BufferedReader fileReader = null;
        File file = null;
        Map<String, String> result = new HashMap<>();
        try {
            file = new File(path);
            fileReader = new BufferedReader(new FileReader(file));
            while (true) {
                String line = fileReader.readLine();
                if (Objects.isNull(line))
                    break;
                result.put(line.split(":")[0].trim(), line.split(":")[1].trim());
            }
        } finally {
            if (file != null)
                fileReader.close();
        }
        return result;
    }
}