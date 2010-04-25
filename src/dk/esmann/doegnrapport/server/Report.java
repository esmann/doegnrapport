package dk.esmann.doegnrapport.server;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Report
{

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    protected Date date;

    @Persistent
    protected String title;

    @Persistent
    protected String locationDescription;

    @Persistent
    protected Text description;

    @Persistent
    protected ReportType type;

    @Persistent
    protected GeoPt location;

    @NotPersistent
    protected String report;

    public Report(String report)
    {
        this.report = report;
    }

    public abstract void parseAndStore();

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

    public String getLocationDescription()
    {
        return locationDescription;
    }

    public void setLocationDescription(String location)
    {
        this.locationDescription = location;
    }

    public Text getDescription()
    {
        return description;
    }

    public void setDescription(Text description)
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

}
