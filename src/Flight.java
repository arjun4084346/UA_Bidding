import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;


public class Flight {
  List<String> layovers;
  String number;
  int crInMinutes;
  double perDiem;
  int tafbInMinutes;
  int totalTimeInMinutes;
  double totalPay;
  double totalPayPerHour;
  String reportingTime;
  String releaseTime;
  int blockedDays;

  public Flight() {
    this.layovers = new ArrayList<>();
    Main.MYFORMAT.setMaximumFractionDigits(2);
  }

  public void addSector(String line) {
  }

  public void addCr(String cr) {
    this.crInMinutes = getMinutes(cr);
  }

  static int getMinutes(String cr) {
    String[] time = cr.split(":");
    return  60 * Integer.parseInt(time[0]) + Integer.parseInt(time[1]);
  }

  public void addPerDiem(String perDiem) {
    this.perDiem = Double.parseDouble(perDiem);
  }

  public void addTafb(String tafb) {
    this.tafbInMinutes = getMinutes(tafb);
  }

  public void finalizeFlight() {
    this.totalTimeInMinutes = this.tafbInMinutes + 2 * Main.COMMUTE_TIME_IN_MINUTES;
    this.totalPay = this.perDiem + ((this.crInMinutes / 60.0) * Main.PAY_PER_HOUR);
    this.totalPayPerHour = this.totalPay / (this.totalTimeInMinutes / 60.0);
  }

  @Override
  public String toString() {
    return String.format(this.number + " Total Pay/Hour = %.2f" +
        "   Total Pay = $%.0f   Total time = "
        + Main.minutesToHHmm(this.totalTimeInMinutes), this.totalPayPerHour, this.totalPay)
        + "   Reporting time " + this.reportingTime;
  }

  public boolean isValid() {
    return !Strings.isNullOrEmpty(number) && tafbInMinutes > 0 && totalPay > 0 && totalTimeInMinutes > 0 && perDiem > 0 && crInMinutes > 0;
  }
}
