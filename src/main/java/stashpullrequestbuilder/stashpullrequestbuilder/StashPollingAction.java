package stashpullrequestbuilder.stashpullrequestbuilder;

import hudson.Functions;
import hudson.model.Action;
import hudson.model.Job;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.jelly.XMLOutput;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.xml.sax.SAXException;

/**
 * Add "Polling Log" item to the project menu.
 *
 * <p>The class provides a method to log messages and exceptions that supports slf4j
 * MessageFormatter patterns.
 */
public class StashPollingAction implements Action {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z");

  private final Job<?, ?> owner;
  private StringWriter stringWriter;
  private PrintWriter printWriter;

  public StashPollingAction(final Job<?, ?> job) {
    this.owner = job;
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
  }

  @Override
  public String getIconFileName() {
    return "clipboard.png";
  }

  @Override
  public String getDisplayName() {
    return "Polling Log";
  }

  @Override
  public String getUrlName() {
    return "stash-polling";
  }

  public Job<?, ?> getOwner() {
    return owner;
  }

  public void log(String pattern, Object... arguments) {
    String timestamp = ZonedDateTime.now().format(TIMESTAMP_FORMATTER);
    FormattingTuple tuple = MessageFormatter.arrayFormat(pattern, arguments);

    String logEntry = tuple.getMessage();
    printWriter.println(timestamp + " " + logEntry);

    Throwable throwable = tuple.getThrowable();
    if (throwable != null) {
      Functions.printStackTrace(throwable, printWriter);
    }
  }

  public void resetLog() {
    printWriter.flush();
    stringWriter.getBuffer().setLength(0);
  }

  @Override
  public String toString() {
    printWriter.flush();
    return stringWriter.toString();
  }

  public void writeLogTo(XMLOutput out) throws SAXException {
    out.write(this.toString());
  }
}
