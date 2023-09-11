import java.util.Comparator;


public class FlightsComparatorByLayoverDuration implements Comparator<Flight> {

  // higher totalPayPerHour should be a smaller number for sorting algorithm
  @Override
  public int compare(Flight flight1, Flight flight2) {
    return flight2.reportingTime.compareTo(flight1.reportingTime);
  }
}
