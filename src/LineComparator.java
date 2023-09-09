import java.util.Comparator;


public class LineComparator implements Comparator<Line>  {

  // higher totalPayPerHour should be a smaller number for sorting algorithm

  @Override
  public int compare(Line line1, Line line2) {
    return Double.compare(line2.totalPayPerHour, line1.totalPayPerHour);
  }
}
