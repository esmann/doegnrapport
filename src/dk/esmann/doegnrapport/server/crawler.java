package dk.esmann.doegnrapport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;

public class crawler
{

    private static final Logger log = Logger.getLogger(crawler.class.getName());

    @SuppressWarnings("finally")
    public static String getReports()
    {
        PersistenceManager persistenceManager = PMF.get().getPersistenceManager();

        try
        {
            URL url = new URL("http://www.politi.dk/Koebenhavn/da/lokalnyt/Doegnrapporter/doegnrapport_220410.htm");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null)
            {
                // log.warning("line: " + line);
                Pattern reportPattern = Pattern.compile("Articlewithindexpagecontrol_XMLliste1");
                Matcher reportMatcher = reportPattern.matcher(line);
                if (reportMatcher.find())
                {
                    log.warning("we found the report line");
                    String reports[] = line.split("<h3>");
                    log.warning("found " + reports.length + "reports");
                    for (String report : reports)
                    {

                    }
                }
            }
            reader.close();

        } catch (MalformedURLException e)
        {
            // ...
            log.log(Level.WARNING, "malformed url", e);
        } catch (IOException e)
        {
            // ...
            log.log(Level.WARNING, "io exception", e);
        } finally
        {
            return "";
        }
    }
}
