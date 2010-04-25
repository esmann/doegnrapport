package dk.esmann.doegnrapport.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Report
{

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private Date date;

    @Persistent
    private String title;

    @Persistent
    private String location;

    @Persistent
    private String description;

    @Persistent
    private ReportType type;

    public Report(String title, String date, String content)
    {
    }

    public void parsePoliceReport(String report)
    {
        Pattern pattern = Pattern.compile("<a.*?>(.*?)</a></h3><br/>(.*?)<BR><BR>(.*?)<a");
        Matcher matcher = pattern.matcher(report);
        if (matcher.find())
        {
            Report reportObject = new Report(matcher.group(1), matcher.group(2), matcher.group(3));
            String titleParts[] = matcher.group(1).split(", ");
            this.title = titleParts[0];
            this.type = getTypeFromTitle(titleParts[0]);
            this.location = titleParts[1];
            this.date = getDateFromString(matcher.group(2));
            this.description = matcher.group(3);
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

    private ReportType getTypeFromTitle(String string)
    {
        return ReportType.ANDET;
    }

    public Key getKey()
    {
        return key;
    }

    public void setKey(Key key)
    {
        this.key = key;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ReportType getType()
    {
        return type;
    }

    public void setType(ReportType type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Report [date=" + date + ", description=" + description + ", key=" + key + ", location=" + location + ", title=" + title + ", type=" + type + "]";
    }

    public static void main(String[] args)
    {
        Report report = new Report("Røveri mod person, Hovedbanegården", "torsdag den 22. april 2010 kl. 2235.", "empty");
        System.out.println(report.toString());
    }

}
