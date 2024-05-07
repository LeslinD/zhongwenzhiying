package util;

import exce.NullDataToEscapeException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Convert2CSV {
    public static void ConvertToCSV(List<List<String>> lines, String outputFileName) {
        List<String> escapedLines = new ArrayList<>();
        try {
            lines.forEach(line -> {
                List<String> words = new ArrayList<>();
                line.forEach( word -> {
                    try {
                        words.add(EscapeSpecialCharacters(word));
                    } catch (NullDataToEscapeException e) {
                        throw new RuntimeException(e);
                    }
                });
                escapedLines.add(String.join(",", words));
            });
            PrintWriter pw = new PrintWriter(new FileWriter(outputFileName));
            pw.print(String.join("\n", escapedLines));
            pw.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String EscapeSpecialCharacters(String line) throws NullDataToEscapeException {
        if (line == null) {
            throw new NullDataToEscapeException();
        }
        String escapedLine = line.replaceAll("\\R", " ");
        if (line.contains(",") || line.contains("\"") || line.contains("'")) {
            line = line.replace("\"", "\"\"");
            escapedLine = "\"" + line + "\"";
        }
        return escapedLine;
    }
}
