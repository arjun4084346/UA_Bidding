import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;


/**
 * Verify the assumptions
 * 1) per diem is paid during flight hours?
 *
 */
public class Main {

  static final int COMMUTE_TIME_IN_MINUTES = 30;
  static final double PAY_PER_HOUR = 28.33;
  static final double PER_DIEM = 2.35;

  static final int COLUMN_SEPARATOR = 68;
  static int lineNumber = 0;
  static final String LINES_SEPARATOR = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------";
  static final String RESERVE_SEPARATOR = "-----------------------------------------------------------------------------------------------------------------------------------------------";
  static final Splitter LIST_SPLITTER = Splitter.onPattern(" ").trimResults().omitEmptyStrings();

  public static NumberFormat MYFORMAT = NumberFormat.getInstance();
  static final String FILE = "/Users/abora/Downloads/f_sfo_pri_d_2309.txt"; // Replace with your file path
  static Map<String, Flight> allFlights = new HashMap<>();
  static List<Line> allLines = new ArrayList<>();
  static List<Flight> sortedFlights;

  static {
    loadFlights(allFlights);
    loadLines(allLines);
  }

  public static void main(String[] args) {
    linesDefaultSorting();
    flightsDefaultSorting();

    System.out.println("Median Pay/Hour for a flight plan = " + MYFORMAT.format(sortedFlights.get(sortedFlights.size()/2).totalPayPerHour));
  }

  private static void loadLines(List<Line> allLines) {
    LineExtractor lineExtractor = new LineExtractor();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(FILE));

      String line;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (line.equals(LINES_SEPARATOR)) {
          break;
        }
      }

      while ((line = reader.readLine()) != null) {
        if (line.equals(RESERVE_SEPARATOR)) {
          break;
        }
        lineExtractor.feedLine(line);
      }

      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("An error occurred while reading the file.");
    }

    allLines.addAll(lineExtractor.lines);
  }

  @Test
  public static void linesDefaultSorting() {
    allLines.sort(new LineComparator());
    int rank = 1;
    for (Line line : allLines) {
      System.out.printf("Rank %3d   " + line + "\n", rank++);
    }
  }

  @Test
  public static void linesSortingByCalenderDays() {
    allLines.sort(new LineComparatorByCalenderDays());
    int rank = 1;
    for (Line line : allLines) {
      System.out.printf("Rank %3d   " + line + "\n", rank++);
    }
  }

  @Test
  public static void flightsDefaultSorting() {
    sortedFlights = allFlights.values().stream().sorted(new FlightsComparator()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void flightsByReportingTime() {
    sortedFlights = allFlights.values().stream().sorted(new FlightsComparatorReportingTime()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  private static void loadFlights(Map<String, Flight> allFlights) {
    FlightExtractor flightExtractor1 = new FlightExtractor();
    FlightExtractor flightExtractor2 = new FlightExtractor();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(FILE));

      String line;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (line.startsWith(LINES_SEPARATOR)) {
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

    allFlights.putAll(flightExtractor1.flights);
    allFlights.putAll(flightExtractor2.flights);
  }

  public static String minutesToHHmm(int minutes) {
    return StringUtils.leftPad("" + minutes/60, 3, ' ') + ":" + StringUtils.leftPad("" + minutes%60, 2, '0');
  }
}