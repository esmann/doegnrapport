package dk.esmann.doegnrapport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private GeoPt getLocation()
    {
        GeoPt location = new GeoPt(0F, 0F);
        // TODO add support for other communities than Copenhagen.
        try
        {
            URL url = new URL("http://maps.google.com/maps/api/geocode/json?address=" + locationDescription + ",copenhagen,denmark&sensor=false");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line, jsonString = "";
            while ((line = reader.readLine()) != null)
            {
                jsonString += line;
            }
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.getString("status").equalsIgnoreCase("ok"))
            {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 1)
                {
                    log.info("Found more than one result for: " + locationDescription + " choosing the first one");
                }
                JSONObject result = results.getJSONObject(0);
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject locationObject = geometry.getJSONObject("location");
                String latitude = locationObject.getString("lat");
                String longitude = locationObject.getString("lng");
                location = new GeoPt(Float.parseFloat(latitude), Float.parseFloat(longitude));

            } else
            {
                log.info("couldn't get a location for: " + locationDescription + " the returned json was: \n" + jsonObject.toString(2));
            }

        } catch (IOException e)
        {
            log.log(Level.INFO, "getlocation failed: " + e.getMessage(), e);
        } catch (JSONException e)
        {
            log.log(Level.WARNING, "error parsing json: " + e.getMessage(), e);
        }

        return location;
    }

    private ReportType getTypeFromTitle(String string)
    {
        return ReportType.ANDET;
    }

}
