import java.util.Comparator;


public class LineComparatorWithEarlyHoursPenalty implements Comparator<Line>  {

  // higher totalPayPerHour should be a smaller number for sorting algorithm

  @Override
  public int compare(Line line1, Line line2) {
    double adjustPayPerHourFlight1 = (line1.totalPay - line1.earlyHourPenalty)/ (line1.totalTimeInMinutes / 60.0);
    double adjustPayPerHourFlight2 = (line2.totalPay - line2.earlyHourPenalty) / (line2.totalTimeInMinutes / 60.0);
    return Double.compare(adjustPayPerHourFlight2, adjustPayPerHourFlight1);
  }
}
