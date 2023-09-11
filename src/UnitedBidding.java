import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;
import org.testng.annotations.Test;

/**
 * Verify the assumptions
 * 1) per diem is paid during flight hours?
 * 2) TDUTY looks like the time from reporting to release, i.e. TBLK + GRND + time before 1st flight + 15 min after last flight
 * This looks like a better metrics to use in pay per hour
 *
 */
public class UnitedBidding {
  static final int COMMUTE_TIME_IN_MINUTES = 30;
  static final int REST_TIME_IN_MINUTES = 900;
  static final double PAY_PER_HOUR = 28.88;
  static final double PER_DIEM = 2.35;

  static final String FILE = "/Users/abora/Downloads/f_sfo_pri_d_2309.txt"; // Replace with your file path

  public static void main(String[] args) {
    linesDefaultSorting();
    System.out.println();
    flightsDefaultSorting();
    System.out.println();

    System.out.printf("Median Pay/Hour for a flight plan = %.2f\n", Utils.sortedFlights.get(Utils.sortedFlights.size()/2).totalPayPerHour);
    int totalWaitTime = Utils.allFlights.values().stream().map(flight -> flight.waitTimeInMinutes).reduce(0, Integer::sum);
    int numberOfFlightsWithGroundTime = Utils.allFlights.values().stream().map(flight -> flight.numberOfFlightsWithGroundTime).reduce(0, Integer::sum);
    int numberOfFlights = Utils.allFlights.values().stream().map(flight -> flight.numberOfFlights).reduce(0, Integer::sum);
    if (numberOfFlightsWithGroundTime > 0) {
      System.out.println("Average ground time per flight = " + Utils.minutesToHHmm(totalWaitTime / numberOfFlightsWithGroundTime));
      System.out.printf("%d out of %d flights are terminal that means no flight time, this reduces Average ground time per flight to %s",
          numberOfFlights-numberOfFlightsWithGroundTime, numberOfFlights, Utils.minutesToHHmm(totalWaitTime / numberOfFlights));
    }
  }

  @Test
  public static void linesDefaultSorting() {
    Utils.allLines.sort(new LineComparator());
    int rank = 1;
    for (Line line : Utils.allLines) {
      System.out.printf("Rank %3d   " + line + "\n", rank++);
    }
  }

  @Test
  public static void flightsDefaultSorting() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparator()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void linesSortingPayPerCalenderDay() {
    Utils.allLines.sort(new LineComparatorByCalenderDays());
    int rank = 1;
    for (Line line : Utils.allLines) {
      System.out.printf("Rank %3d   " + line + "\n", rank++);
    }
  }

  @Test
  public static void flightsSortingPayPerCalenderDay() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorByCalenderDays()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void flightsByLayoverDuration() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorByLayoverDuration()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void flightsByTotalPay() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorByTotalPay()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void flightsByReportingTime() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorReportingTime()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void flightsByGroundTime() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorGroundTime()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
    }
  }

  @Test
  public static void listOfLayovers() {
    Utils.layovers = Utils.layovers.stream().sorted(new LayoverComparator()).collect(Collectors.toList());
    int rank = 1;
    for (Triple<String, String, String> layover : Utils.layovers) {
      System.out.printf("Rank %3d  " + layover.getLeft() + "  " + layover.getMiddle() + "  " + layover.getRight() + "\n", rank++);
    }
  }
}