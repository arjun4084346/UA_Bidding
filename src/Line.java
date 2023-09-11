import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;


public class Line {
  String number;
  List<String> flights;
  int crInMinutes;
  double perDiem;
  int tafbInMinutes;
  int totalTimeInMinutes;
  int blockedDays = 0;
  double totalPay;
  double totalPayPerHour;
  int waitTimeInMinutes = 0;
  int waitTimeInMinutesPerFlight;

  public Line() {
    this.flights = new ArrayList<>();
  }

  public void finalizeLine() {
    this.perDiem = UnitedBidding.PER_DIEM * (this.tafbInMinutes / 60.0);
    this.totalTimeInMinutes = this.tafbInMinutes + 2 * this.flights.size() * UnitedBidding.COMMUTE_TIME_IN_MINUTES + UnitedBidding.REST_TIME_IN_MINUTES;
    this.totalPay = this.perDiem + ((this.crInMinutes / 60.0) * UnitedBidding.PAY_PER_HOUR);
    this.totalPayPerHour = this.totalPay / (this.totalTimeInMinutes / 60.0);
    this.blockedDays = this.flights.stream().map(flightStr -> Utils.allFlights.get(flightStr).blockedDays).reduce(0, Integer::sum);
    this.waitTimeInMinutes = this.flights.stream().map(flightStr -> Utils.allFlights.get(flightStr).waitTimeInMinutes).reduce(0, Integer::sum);
    int numberOfFlightsWithGroundTime = this.flights.stream().map(flightStr -> Utils.allFlights.get(flightStr).numberOfFlightsWithGroundTime).reduce(0, Integer::sum);
    if (numberOfFlightsWithGroundTime > 0) {
      this.waitTimeInMinutesPerFlight = this.waitTimeInMinutes / (numberOfFlightsWithGroundTime);
    }
  }

  public boolean isValid() {
    return !Strings.isNullOrEmpty(number) && !flights.isEmpty();
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder(String.format(
        "%4s Total Pay/Hour = " + StringUtils.leftPad(String.format("$%.2f", this.totalPayPerHour), 6, ' ') +
            "   Total Pay = $%4.0f" +
            "   Total time = " + Utils.minutesToHHmm(this.totalTimeInMinutes) +
            "   Total ground time = " + Utils.minutesToHHmm(this.totalTimeInMinutes) +
            "   Total flights = " + StringUtils.leftPad("" + this.flights.size(), 2, ' ') +
            "   Total calender days = " + StringUtils.leftPad("" + this.blockedDays, 2, ' ') +
            //(this.waitTimeInMinutesPerFlight == 0 ? "" : "   Total ground time per flight = " + Main.minutesToHHmm(this.waitTimeInMinutesPerFlight)),
            "   Total ground time per flight = " + Utils.minutesToHHmm(this.waitTimeInMinutesPerFlight),
        this.number, this.totalPay, this.blockedDays) + "   ");

    for (String flight : this.flights.stream().distinct().collect(Collectors.toList())) {
      sb.append(flight)
          .append(" = ")
          .append(Utils.allFlights.get(flight).reportingTime)
          .append("-")
          .append(Utils.allFlights.get(flight).releaseTime)
          .append("  ");
    }
    return sb.toString();
  }
}
