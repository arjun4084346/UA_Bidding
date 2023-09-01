import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class Main {

  static final int COMMUTE_TIME = 30;
  static final double PAY_PER_HOUR = 28.33;

  static final int COLUMN_SEPARATOR = 68;
  static int lineNumber = 0;
  public static NumberFormat MYFORMAT = NumberFormat.getInstance();

  public static void main(String[] args) {
    String filePath = "/Users/abora/Downloads/f_sfo_pri_i_2309.txt"; // Replace with your file path
    FlightExtractor flightExtractor1 = new FlightExtractor();
    FlightExtractor flightExtractor2 = new FlightExtractor();
    List<Flight> allFlights = new ArrayList<>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));

      String line;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (line.startsWith("AUGUST")) {
          break;
        }
        if (line.length() < COLUMN_SEPARATOR) {
          continue;
        }
        flightExtractor1.feedLine(line.substring(0, COLUMN_SEPARATOR).trim());
        flightExtractor2.feedLine(line.substring(COLUMN_SEPARATOR).trim());
      }

      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("An error occurred while reading the file.");
    }

    allFlights.addAll(flightExtractor1.flights);
    allFlights.addAll(flightExtractor2.flights);

    allFlights.sort(new FlightsComparator());

    for (Flight flight : allFlights) {
      System.out.println(flight);
    }

    System.out.println("Median Pay/Hour = " + MYFORMAT.format(allFlights.get(allFlights.size()/2).totalPayPerHour));
  }
}