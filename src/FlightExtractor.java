import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;


public class FlightExtractor {
  int dhd = 0; // dead head
  int tafb = 0; // time away from base
  int cr = 0;
  String flight = "";
  double perDiem = 0.0;
  static final String SEPARATOR = "---";
  List<Flight> flights = new ArrayList<>();
  Flight currentFlight = new Flight();
  private static final Splitter LIST_SPLITTER = Splitter.onPattern(" ").trimResults().omitEmptyStrings();

  public void feedLine(String line) {
    if (Strings.isNullOrEmpty(line)) {
      return;
    }
    List<String> tokens = LIST_SPLITTER.splitToList(line);
    if (line.startsWith(SEPARATOR)) {
      Flight flight = finishCurrentFlight();
      if (flight.isValid()) {
        flights.add(flight);
      }
      reset();
    } else if (line.startsWith("F")) {
      currentFlight.number = tokens.get(0);
    } else if (Character.isDigit(line.charAt(0))) {
      currentFlight.addSector(line);
    } else if (line.startsWith("TOTAL CR")) {
      currentFlight.addCr(tokens.get(2));
    } else if (line.startsWith("TOTAL DHD")) {
      currentFlight.addTafb(tokens.get(4));
    } else if (line.startsWith("TOTAL RIG")) {
      currentFlight.addPerDiem(tokens.get(6));
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
