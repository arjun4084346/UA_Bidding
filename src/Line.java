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

  public Line() {
    this.flights = new ArrayList<>();
  }

  public void finalizeLine() {
    this.perDiem = Main.PER_DIEM * (this.tafbInMinutes / 60.0);
    this.totalTimeInMinutes = this.tafbInMinutes + 2 * this.flights.size() * Main.COMMUTE_TIME_IN_MINUTES;
    this.totalPay = this.perDiem + ((this.crInMinutes / 60.0) * Main.PAY_PER_HOUR);
    this.totalPayPerHour = this.totalPay / (this.totalTimeInMinutes / 60.0);
    this.blockedDays = this.flights.stream().map(flightStr -> Main.allFlights.get(flightStr).blockedDays).reduce(0, Integer::sum);
  }

  public boolean isValid() {
    return !Strings.isNullOrEmpty(number) && !flights.isEmpty();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(String.format("%4s Total Pay/Hour = $%.2f" +
        "   Total Pay = $%.0f   Total time = " + Main.minutesToHHmm(this.totalTimeInMinutes) +
        "   Total flights = " + StringUtils.leftPad("" + this.flights.size(), 2, ' ') +
        "   Total calender days = " + StringUtils.leftPad("" + this.blockedDays, 2, ' '),
        this.number, this.totalPayPerHour, this.totalPay, this.blockedDays) + "   ");
    for (String flight : this.flights.stream().distinct().collect(Collectors.toList())) {
        sb.append(flight).append(" = ")
          .append(Main.allFlights.get(flight).reportingTime)
          .append("-")
          .append(Main.allFlights.get(flight).releaseTime)
          .append("  ");
    }
    return sb.toString();
  }
}
