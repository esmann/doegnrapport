package dk.esmann.doegnrapport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportPage
{

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private BufferedReader reader;

    public ReportPage(BufferedReader reportPageReader)
    {
        this.reader = reportPageReader;
    }

    public void parseAndStore()
    {
        String line;

        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                // log.warning("line: " + line);
                Pattern reportPattern = Pattern.compile("Articlewithindexpagecontrol_XMLliste1");
                Matcher reportMatcher = reportPattern.matcher(line);
                if (reportMatcher.find())
                {
                    log.info("we found the report line");
                    String reports[] = line.split("<h3>");
                    log.info("found " + reports.length + "reports");
                    for (String report : reports)
                    {
                        PoliceReport policeReport = new PoliceReport(report);
                        policeReport.parseAndStore();
                    }
                }
            }
            this.reader.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
