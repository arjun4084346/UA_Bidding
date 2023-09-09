import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FlightExtractor {
  int dhd = 0; // dead head
  int tafb = 0; // time away from base
  int cr = 0;
  String flight = "";
  double perDiem = 0.0;
  static final String SEPARATOR = "---";
  Map<String, Flight> flights = new HashMap<>();
  Flight currentFlight = new Flight();

  public void feedLine(String line) {
    try {
      if (Strings.isNullOrEmpty(line)) {
        return;
      }
      List<String> tokens = Main.LIST_SPLITTER.splitToList(line);
      if (line.startsWith(SEPARATOR)) {
        Flight flight = finishCurrentFlight();
        if (flight.isValid()) {
          flights.put(flight.number, flight);
        }
        reset();
      } else if (line.startsWith("F") && line.contains("RPT -->")) {
        currentFlight.number = tokens.get(0);
        currentFlight.reportingTime = tokens.get(2).substring(3);
      } else if (Character.isDigit(line.charAt(0)) && line.charAt(1) == ' ') {
        currentFlight.addSector(line);
        currentFlight.releaseTime = tokens.get(line.contains("*") ? 5 : 6);
        currentFlight.blockedDays = Integer.parseInt(line.substring(0,1));
      } else if (line.startsWith("TOTAL CR")) {
        currentFlight.addCr(tokens.get(2));
      } else if (line.startsWith("TOTAL DHD")) {
        currentFlight.addTafb(tokens.get(4));
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
