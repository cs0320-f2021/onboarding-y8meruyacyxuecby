package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * CSVReader class to parse information from CSV into a list of Star objects.
 */
public class CSVReader {

  public CSVReader() {

  }

  /**
   *
   * @param file
   * @return
   */
  public ArrayList<Star> readCSV(String file) {
    try {
      // parse CSV into BufferedReader
      BufferedReader br = new BufferedReader(new FileReader(file));
      if (!br.readLine().equals("StarID,ProperName,X,Y,Z")) {
        throw new IOException("ERROR: File does not contain correct columns");
      }
      String line;

      ArrayList<Star> stars = new ArrayList<>();

      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        Star newStar = new Star(
            Integer.parseInt(data[0]),
            data[1],
            Float.parseFloat(data[2]),
            Float.parseFloat(data[3]),
            Float.parseFloat(data[4]));
        stars.add(newStar);
      }
      return stars;
    } catch (IOException e) {
      System.out.println(e);
      return null;
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Row does not contain correct data types");
      return null;
    }
  }
}
