import java.util.Comparator;


public class FlightsComparatorWithEarlyHoursPenalty implements Comparator<Flight> {

  // higher totalPayPerHour should be a smaller number for sorting algorithm
  @Override
  public int compare(Flight flight1, Flight flight2) {
    double adjustPayPerHourFlight1 = (flight1.totalPay - flight1.earlyHourPenalty) / (flight1.totalTimeInMinutes / 60.0);
    double adjustPayPerHourFlight2 = (flight2.totalPay - flight2.earlyHourPenalty) / (flight2.totalTimeInMinutes / 60.0);
    return Double.compare(adjustPayPerHourFlight2, adjustPayPerHourFlight1);
  }
}
