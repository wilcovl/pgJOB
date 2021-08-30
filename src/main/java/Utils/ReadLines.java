package Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Utils.ReadLine;
import com.opencsv.CSVReader;

public abstract class ReadLines {

    public static String arrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        if (arr.length > 0) {
            sb.append(arr[0]);
        }
        for (int i=1; i<arr.length; i++) {
            sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public abstract void processLine(String[] line);

    public static void readCsv(String fileLoc, ReadLine readLine) {
        try {
            CSVReader reader = new CSVReader(new FileReader(fileLoc));
            String[] line;
            int i= 0;
            while ((line = reader.readNext()) != null) {
                readLine.read(line, i);
                i++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
