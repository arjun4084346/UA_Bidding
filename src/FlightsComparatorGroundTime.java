import java.util.Comparator;


public class FlightsComparatorGroundTime implements Comparator<Flight> {

  // higher totalPayPerHour should be a smaller number for sorting algorithm
  @Override
  public int compare(Flight flight1, Flight flight2) {
    return Integer.compare(flight1.waitTimeInMinutesPerFlight, flight2.waitTimeInMinutesPerFlight);
  }
}
