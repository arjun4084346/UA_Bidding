import java.util.Comparator;


public class FlightsComparator implements Comparator<Flight> {

  @Override
  public int compare(Flight flight1, Flight flight2) {
    return Double.compare(flight1.totalPayPerHour, flight2.totalPayPerHour);
  }
}
