import com.google.common.base.Strings;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class Flight {
  List<String> layovers;
  String number;
  int cr;
  double perDiem;
  int tafb;
  int totalTime;
  double totalPay;
  double totalPayPerHour;

  public Flight() {
    this.layovers = new ArrayList<>();
    Main.MYFORMAT.setMaximumFractionDigits(2);
  }

  public void addSector(String line) {
  }

  public void addCr(String cr) {
    this.cr = getMinutes(cr);
  }

  private static int getMinutes(String cr) {
    String[] time = cr.split(":");
    return  60 * Integer.parseInt(time[0]) + Integer.parseInt(time[1]);
  }

  public void addPerDiem(String perDiem) {
    this.perDiem = Double.parseDouble(perDiem);
  }

  public void addTafb(String tafb) {
    this.tafb = getMinutes(tafb);
  }

  public void finalizeFlight() {
    this.totalTime = this.tafb + 2 * Main.COMMUTE_TIME;
    this.totalPay = this.perDiem + ((this.cr/60.0) * Main.PAY_PER_HOUR);
    this.totalPayPerHour = this.totalPay / (this.totalTime / 60.0);
  }

  @Override
  public String toString() {
    return this.number + " Total Pay/Hour = " + Main.MYFORMAT.format(this.totalPayPerHour) +
        "   Total Pay = $" + Main.MYFORMAT.format(this.totalPay) + "   Total time = " + minutesToHHmm(this.totalTime);
  }

  public boolean isValid() {
    return !Strings.isNullOrEmpty(number) && tafb > 0 && totalPay > 0 && totalTime > 0 && perDiem > 0 && cr > 0;
  }

  public String minutesToHHmm(int minutes) {
    return minutes/60 + ":" + minutes%60;
  }
}
