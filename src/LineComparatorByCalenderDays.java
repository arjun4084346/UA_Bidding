import java.util.Comparator;


public class LineComparatorByCalenderDays implements Comparator<Line>  {

  // higher totalPayPerHour should be a smaller number for sorting algorithm

  @Override
  public int compare(Line line1, Line line2) {
    return Double.compare(line2.totalPay / line2.blockedDays, line1.totalPay / line1.blockedDays);
  }
}
