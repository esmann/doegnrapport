package dk.esmann.doegnrapport.server;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class PoliceReport extends Report
{

    public PoliceReport(String report)
    {
        super(report);
    }

    @Override
    public void parseAndStore()
    {
        parse();
        store();
    }

    private void parse()
    {
        Pattern pattern = Pattern.compile("<a.*?>(.*?)</a></h3><br/>(.*?)<BR><BR>(.*?)<a");
        Matcher matcher = pattern.matcher(this.report);
        if (matcher.find())
        {
            String titleParts[] = matcher.group(1).split(", ");
            this.title = titleParts[0];
            this.type = getTypeFromTitle(titleParts[0]);
            this.locationDescription = titleParts[1];
            this.location = getLocation();
            this.date = getDateFromString(matcher.group(2));
            this.description = new Text(matcher.group(3));
        }
    }

    private void store()
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try
        {
            log.info(toString());
            pm.makePersistent(this);
        } catch (Exception e)
        {
            log.log(Level.WARNING, "error saving report", e);
        } finally
        {
            pm.close();
        }
    }

    private Date getDateFromString(String date)
    {
        Locale locale = new Locale("da", "DK");
        SimpleDateFormat parser = new SimpleDateFormat("EEE 'den' dd. MMMM yyyy 'kl.' HHmm", locale);
        try
        {
            Date newDate = parser.parse(date);
            return newDate;
        } catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();

    }

    @SuppressWarnings("unchecked")
    private GeoPt getLocation()
    {
        // TODO add support for other communities than Copenhagen.
        try
        {
            URL url = new URL("http://maps.google.com/maps/api/geocode/json?address=" + locationDescription + ",copenhagen,denmark&sensor=false");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = (Map<String, Object>) mapper.readValue(url, Object.class);
            log.info("parsed json");
            if (map.get("status").equals("OK"))
            {
                Map<String, Object> results = (Map<String, Object>) map.get("results");
                if (results.containsKey("geometry"))
                {
                    log.info("Found geometry");
                    Map<String, Object> geometry = (Map<String, Object>) map.get("geometry");
                    if (geometry.containsKey("location"))
                    {
                        log.info("Found location");
                        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
                        log.info("lat: " + location.get("lat") + " lng: " + location.get("lng"));
                    }
                }
            }
            map.isEmpty();

        } catch (IOException e)
        {
            log.log(Level.INFO, "getlocation failed: " + e.getMessage(), e);
        }

        Float latitude = 0F, longitude = 0F;
        GeoPt location = new GeoPt(latitude, longitude);
        return null;
    }

    private ReportType getTypeFromTitle(String string)
    {
        return ReportType.ANDET;
    }

}
