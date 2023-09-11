import java.util.Comparator;
import org.apache.commons.lang3.tuple.Triple;


public class LayoverComparator implements Comparator<Triple<String, String, String>>  {

  // higher totalPayPerHour should be a smaller number for sorting algorithm

  @Override
  public int compare(Triple<String, String, String> layover1, Triple<String, String, String> layover2) {
    return layover2.getMiddle().compareTo(layover1.getMiddle());
  }
}
