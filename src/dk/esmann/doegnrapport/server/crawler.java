package dk.esmann.doegnrapport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class crawler
{

    private static final Logger log = Logger.getLogger(crawler.class.getName());

    @SuppressWarnings("finally")
    public static String getReports()
    {
        try
        {
            // TODO add support for getting reports from other dates
            // TODO Either by parsing index.html or by saving the newest parsed date and try to fetch all date between that and now
            URL url = new URL("http://www.politi.dk/Koebenhavn/da/lokalnyt/Doegnrapporter/doegnrapport_220410.htm");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ReportPage reportPage = new ReportPage(reader);
            reportPage.parseAndStore();

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
