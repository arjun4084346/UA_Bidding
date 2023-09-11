import java.util.Comparator;


public class FlightsComparatorByTotalPay implements Comparator<Flight> {

  // higher totalPayPerHour should be a smaller number for sorting algorithm
  @Override
  public int compare(Flight flight1, Flight flight2) {
    return Double.compare(flight2.totalPay, flight1.totalPay);
  }
}
