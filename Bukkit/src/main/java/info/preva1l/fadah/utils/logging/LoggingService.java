package info.preva1l.fadah.utils.logging;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

@Service
public class LoggingService {
    public static final LoggingService instance = new LoggingService();

    final Logger transactionLogger = Logger.getLogger("AuctionHouse-Transactions");

    @Configure
    public void initLogger() {
        Fadah.getConsole().info("Initialising transaction logger...");

        if (!Config.i().isLogToFile()) return;
        try {
            File logsFolder = new File(Fadah.getInstance().getDataFolder(), "logs");
            if (!logsFolder.exists()) {
                if (!logsFolder.mkdirs()) {
                    Fadah.getConsole().warning("Failed to create logs folder!");
                    return;
                }
            }

            File logFile = new File(logsFolder, "transactions.log");

            String archivedLogNameFormat = "transactions_%s%s";
            if (logFile.exists()) {
                String date = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.now());

                String newFileName = archivedLogNameFormat.formatted(date, "");

                int i = 0;
                while (new File(logsFolder, newFileName + ".log").exists()) {
                    newFileName = archivedLogNameFormat.formatted(date, "-" + ++i);
                }

                File renamedFile = new File(logsFolder, newFileName + ".log");

                if (!logFile.renameTo(renamedFile)) {
                    Fadah.getConsole().warning("Could not rename logfile!");
                }
            }

            FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
            fileHandler.setFormatter(new TransactionLogFormatter());
            transactionLogger.setUseParentHandlers(false);
            for (Handler handler : transactionLogger.getHandlers()) {
                transactionLogger.removeHandler(handler);
            }
            transactionLogger.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Fadah.getConsole().info("Logger Started!");
    }
}
