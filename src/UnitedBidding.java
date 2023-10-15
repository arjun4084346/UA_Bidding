import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

  static final String EARLY_HOURS_TIME = "09:30";
  static final double EARLY_HOURS_TIME_PENALTY = 30.0;
  static final String CONNECTING_FLIGHT_EARLY_HOURS_TIME = "08:30";
  static final double CONNECTING_FLIGHT_EARLY_HOURS_TIME_PENALTY = 20.0;

  static final String[] FILES = new String[] {
      "/home/arjun/Downloads/f_sfo_pri_d_2311.txt",
      "/home/arjun/Downloads/f_sfo_pri_i_2311.txt"
  };

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
  public static void linesSortingWithEarlyHoursPenalty() {
    Utils.allLines.sort(new LineComparatorWithEarlyHoursPenalty());
    int rank = 1;
//    for (Line line : Utils.allLines) {
//      System.out.printf("Rank %3d   " + line + "\n", rank++);
//    }
    for (Line line : Utils.allLines) {
      if (line.flights.stream().noneMatch(f -> Utils.allFlights.get(f).layovers.containsKey("TLV"))) {
        System.out.println(line.number.substring(1));
      }
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
  public static void flightsSortingWithEarlyHoursPenalty() {
    Utils.sortedFlights = Utils.allFlights.values().stream().sorted(new FlightsComparatorWithEarlyHoursPenalty()).collect(Collectors.toList());
    int rank = 1;
    for (Flight flight : Utils.sortedFlights) {
      System.out.printf("Rank %3d   " + flight + "\n", rank++);
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

  @Test (enabled = false) // buggy
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

  @Test
  public static void reportingTimes() {
    Map<String, Integer> reportingTimes = new TreeMap<>();
    Utils.allFlights.values().stream().map(flight -> flight.reportingTime).forEach(time -> reportingTimes.put(time, reportingTimes.getOrDefault(time, 0) + 1));
    for (Map.Entry<String, Integer> entry : reportingTimes.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      System.out.println(key + ": " + value);
    }

    reportingTimes.clear();

    Utils.allFlights.values().stream().map(flight -> flight.furtherReportingTimes).flatMap(List::stream).forEach(time -> reportingTimes.put(time, reportingTimes.getOrDefault(time, 0) + 1));
    for (Map.Entry<String, Integer> entry : reportingTimes.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      System.out.println(key + ": " + value);
    }
  }
}