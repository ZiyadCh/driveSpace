package ma.youcode.driveSpace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingDemo {

  private static final Logger log = LoggerFactory.getLogger(LoggingDemo.class);

  public static void main(String[] args) {
    log.info("Application starting...");
    log.debug("This is a debug message");
    log.warn("This is a warning");
    log.error("This is an error message");

    demonstrateLogging();
  }

  public static void demonstrateLogging() {
    log.info("--- Demonstrating SLF4J logging levels ---");
    log.debug("Debug-level detail information");
    log.info("Informational message");
    log.warn("Warning message");
    log.error("Error message");

    String driverName = "kaiser";
    int places = 3;
    log.info("Driver {} is publishing a trajet with {} places", driverName, places);
  }
}
