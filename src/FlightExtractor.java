import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;

public class FlightExtractor {
  int dhd = 0; // dead head
  int tafb = 0; // time away from base
  int cr = 0;
  String flight = "";
  double perDiem = 0.0;
  static final String SEPARATOR = "---";
  Map<String, Flight> flights = new HashMap<>();
  Flight currentFlight = new Flight();
  String previousLine = "";

  public void feedLine(String line) {
    if (line.equals(previousLine)) {
      return;
    } else {
      previousLine = line;
    }

    try {
      if (Strings.isNullOrEmpty(line)) {
        return;
      }
      List<String> tokens = Utils.LIST_SPLITTER.splitToList(line);
      if (line.startsWith(SEPARATOR)) {
        Flight flight = finishCurrentFlight();
        if (flight.isValid()) {
          flights.put(flight.number, flight);
        } else {
          Utils.invalidFlights++;
        }
        reset();
      } else if (line.startsWith("F") && line.contains("RPT -->")) {
        currentFlight.number = tokens.get(0);
        currentFlight.reportingTime = tokens.get(2).substring(3);
      } else if(line.startsWith("RPT")) {
        currentFlight.furtherReportingTimes.add(tokens.get(1).substring(3));
      } else if (Character.isDigit(line.charAt(0)) && line.charAt(1) == ' ') {
        // flight detail
        currentFlight.departureTime = tokens.get(line.contains("*") ? 4 : 5);
        currentFlight.releaseTime = tokens.get(line.contains("*") ? 5 : 6);
        currentFlight.blockedDays = Integer.parseInt(line.substring(0,1)) +
            (currentFlight.departureTime.compareTo(currentFlight.releaseTime) > 0 ? 1 : 0);
        List<String> times = new ArrayList<>();
        List<String> airports = new ArrayList<>();
        for (String token : tokens) {
          if (token.contains(":")) {
            times.add(token);
          } else if (token.length() == 3 && StringUtils.isAlpha(token)) {
            airports.add(token);
          }
        }
        if (times.size() == 2) {
          currentFlight.waitTimeInMinutes += Utils.getMinutes(times.get(1));
          currentFlight.numberOfFlightsWithGroundTime++;
        }
        if (times.size() == 4) {
          currentFlight.numberOfLayovers++;
          Utils.layovers.add(new ImmutableTriple<>(airports.get(1), times.get(3), currentFlight.number));
        }
        currentFlight.numberOfFlights ++;
      } else if (line.startsWith("TOTAL CR")) {
        currentFlight.crInMinutes = Utils.getMinutes(tokens.get(2));
      } else if (line.startsWith("TOTAL DHD")) {
        currentFlight.tafbInMinutes = Utils.getMinutes(tokens.get(4));
      } else if (line.startsWith("TOTAL RIG")) {
        currentFlight.addPerDiem(tokens.get(6));
      }
    } catch (Exception e) {
      System.out.println(line);
    }
  }

  private Flight finishCurrentFlight() {
    currentFlight.finalizeFlight();
    return currentFlight;
  }

  private void reset() {
    dhd = 0;
    tafb = 0;
    cr = 0;
    flight = "";
    perDiem = 0.0;
    currentFlight = new Flight();
  }
}
