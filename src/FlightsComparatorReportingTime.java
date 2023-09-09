import java.util.Comparator;


public class FlightsComparatorReportingTime implements Comparator<Flight> {

  // higher totalPayPerHour should be a smaller number for sorting algorithm
  @Override
  public int compare(Flight flight1, Flight flight2) {
    return flight1.reportingTime.compareTo(flight2.reportingTime);
  }
}
