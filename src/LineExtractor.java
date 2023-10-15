import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class LineExtractor {
  String line;
  Character flightPrefix;
  int crInMinutes;
  int tafbInMinutes;
  Line currentLine = new Line();
  List<Line> lines = new ArrayList<>();

  public LineExtractor(Character flightPrefix) {
    this.flightPrefix = flightPrefix;
  }

  public void feedLine(String lineText) {
    if (Strings.isNullOrEmpty(lineText)) {
      return;
    }
    List<String> tokens = Utils.LIST_SPLITTER.splitToList(lineText);
    if (lineText.startsWith(Utils.LINES_SEPARATOR)) {
      Line line = finishCurrentLine();
      if (line.isValid()) {
        lines.add(line);
      } else {
        Utils.invalidLines++;
      }
      reset();
    } else if (tokens.get(0).equals("LINE")) {
      currentLine.number = "L" + tokens.get(1);
      currentLine.crInMinutes = Utils.getMinutes(tokens.get(3));
    } else if (tokens.get(0).equals("TAFB")) {
      currentLine.tafbInMinutes = Utils.getMinutes(tokens.get(1));
    } else if (tokens.get(0).equals("CREW")) {
      addFlights(tokens, currentLine);
    }
  }

  private void reset() {
    currentLine = new Line();
  }

  private Line finishCurrentLine() {
    currentLine.finalizeLine();
    return currentLine;
  }

  private void addFlights(List<String> tokens, Line currentLine) {
    for (int index=1; index< tokens.size(); index++) {
      String token = tokens.get(index);
      if (token.length() <= 1) {
        continue;
      }
      if (token.equals("NIGHT")) {
        break;
      }
      int i=0;
      while (i+4 <= token.length()) {
        String code = token.substring(i, i+4);
        i += 4;
        if (StringUtils.isAlphanumeric(code)) {
          currentLine.flights.add(this.flightPrefix + code);
        }
      }
    }
  }
}
