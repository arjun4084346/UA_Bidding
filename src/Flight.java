import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {
  int numberOfLayovers = 0;
  String number;
  int crInMinutes;
  double perDiem;
  int tafbInMinutes;
  int totalTimeInMinutes;
  double totalPay;
  double totalPayPerHour;
  String reportingTime;
  String departureTime;
  String releaseTime;
  int blockedDays;
  int waitTimeInMinutes = 0;
  int waitTimeInMinutesPerFlight = 0;
  int numberOfFlights = 0;
  List<Integer> layoverDurations = new ArrayList<>();
  int numberOfFlightsWithGroundTime = 0;

  public void addPerDiem(String perDiem) {
    this.perDiem = Double.parseDouble(perDiem);
  }

  public void finalizeFlight() {
    this.totalTimeInMinutes = this.tafbInMinutes + 2 * UnitedBidding.COMMUTE_TIME_IN_MINUTES + UnitedBidding.REST_TIME_IN_MINUTES;
    this.totalPay = this.perDiem + ((this.crInMinutes / 60.0) * UnitedBidding.PAY_PER_HOUR);
    this.totalPayPerHour = this.totalPay / (this.totalTimeInMinutes / 60.0);
    if (this.numberOfFlights > 0 && this.numberOfFlightsWithGroundTime > 0) {
      this.waitTimeInMinutesPerFlight = this.waitTimeInMinutes / (this.numberOfFlightsWithGroundTime);
    }
  }

  @Override
  public String toString() {
    return String.format(this.number + " Total Pay/Hour = %.2f" +
        "   Total Pay = $%4.0f   Total time = " +
        Utils.minutesToHHmm(this.totalTimeInMinutes), this.totalPayPerHour, this.totalPay) +
//        (this.waitTimeInMinutesPerFlight == 0 ? "" : "   Total ground time per flight =" + Main.minutesToHHmm(this.waitTimeInMinutesPerFlight)) +
        "   Total ground time per flight =" + Utils.minutesToHHmm(this.waitTimeInMinutesPerFlight) +
        "   Total flights = " + StringUtils.leftPad("" + numberOfFlights, 2, ' ') +
        "   Total calender days = " + StringUtils.leftPad("" + this.blockedDays, 2, ' ') +
        "   Reporting time " + this.reportingTime;
  }

  public boolean isValid() {
    return !Strings.isNullOrEmpty(number) && tafbInMinutes > 0 && totalPay > 0 && totalTimeInMinutes > 0 && perDiem > 0 && crInMinutes > 0;
  }
}
