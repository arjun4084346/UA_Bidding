import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;


public class Utils {
  static int lineNumber = 0;
  static final String LINES_SEPARATOR = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------";
  static final String RESERVE_SEPARATOR = "-----------------------------------------------------------------------------------------------------------------------------------------------";
  static final int COLUMN_SEPARATOR = 68;
  static final Splitter LIST_SPLITTER = Splitter.onPattern(" ").trimResults().omitEmptyStrings();

  static String[] months = new String[]{"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST",
      "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
  static Map<String, Flight> allFlights = new HashMap<>();
  static List<Line> allLines = new ArrayList<>();
  static List<Triple<String, String, String>> layovers = new ArrayList<>(); // layover airport, layover duration, flight number
  static List<Flight> sortedFlights;
  static int invalidLines = 0;
  static int invalidFlights = 0;

  static {
    Utils.loadFlights(allFlights);
    Utils.loadLines(allLines);

    Flight reserveFlight = Flight.builder().number("F-XXX").waitTimeInMinutes(0).numberOfFlightsWithGroundTime(0)
        .numberOfLayovers(0).numberOfFlights(0).crInMinutes(300).tafbInMinutes(255).perDiem(0.0).blockedDays(1)
        .reportingTime("00:00").furtherReportingTimes(Collections.emptyList()).build();
    reserveFlight.finalizeFlight();
    allFlights.put("F-XXX", reserveFlight);

    if (invalidLines > 0 || invalidFlights > 0) {
      throw new RuntimeException("");
    }
  }

  public static void loadFlights(Map<String, Flight> allFlights) {
    for (String file : UnitedBidding.FILES) {
      Character flightPrefix = getFlightPrefix(file);
      FlightExtractor flightExtractor1 = new FlightExtractor(flightPrefix);
      FlightExtractor flightExtractor2 = new FlightExtractor(flightPrefix);

      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
          lineNumber++;
          String trimmedline = line.trim();
          if (trimmedline.startsWith("BASE")) {
            do {
              line = reader.readLine().trim();
              lineNumber++;
            } while (!line.startsWith("---"));
            line = reader.readLine();
            lineNumber++;
          }
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
  }

  private static Character getFlightPrefix(String file) {
    if (file.contains("sfo")) {
      return 'F';
    } else if (file.contains("ewr")) {
      return 'E';
    }
    return null;
  }

  public static void loadLines(List<Line> allLines) {
    lineNumber = 0;

    for (String file : UnitedBidding.FILES) {
      Character flightPrefix = getFlightPrefix(file);
      LineExtractor lineExtractor = new LineExtractor(flightPrefix);

      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
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
          if (Arrays.stream(months).anyMatch(line::contains)) {
            reader.readLine();
            line = reader.readLine();
            lineNumber+=2;
          }
          lineExtractor.feedLine(line);
          lineNumber++;
        }

        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("An error occurred while reading the file.");
      }

      allLines.addAll(lineExtractor.lines);
    }
  }

  public static String minutesToHHmm(int minutes) {
    return StringUtils.leftPad("" + minutes/60, 2, ' ') + ":" + StringUtils.leftPad("" + minutes%60, 2, '0');
  }

  public static int getMinutes(String cr) {
    if (cr.startsWith(":")) {
      cr = "0" + cr;
    }
    String[] time = cr.split(":");
    return  60 * Integer.parseInt(time[0]) + Integer.parseInt(time[1]);
  }
}
