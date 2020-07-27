package com.pb.stratus.controller.locator;

/**
 * A very simple class to model a location so that it can be serialized into 
 * JSON.
 * 
 * @author cokirkha
 */
public class Location 
{
    private String id;
    
    private float score;
    
    private String name;
    
    private double x;
    
    private double y;
    
    private String srs;
    

    /**
     * Constructor for an Location object.
     * @param id the unique identifier of this instance.
     * @param score the score given by lucene for this location in comparison to a query.
     * @param name a string representing a formatted location name.
     * @param x the x coordinate of the location.
     * @param y the y coordinate of the location.
     */
    public Location(String id, float score, String name, double x, double y,
            String srs)
    {
        this.id = id;
        this.score = score;
        this.name = name;
        this.x = x;
        this.y = y;
        this.srs = srs;
    }
    
    /**
     * Accessor method for this instance's id attribute.
     * @return the value of the id attribute.
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * Mutator method for this instance's id attribute.
     * @param id the id for this instance.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Accessor method for this instance's score attribute.
     * @return the value of the score attribute.
     */
    public float getScore()
    {
        return score;
    }
    
    /**
     * Mutator method for this instance's score attribute.
     * @param score the score given by lucene for this location in comparison 
     * to a query.
     */
    public void setScore(float score)
    {
        this.score = score;
    }

    /**
     * Accessor method for this instance's name attribute.
     * @return the value of the name attribute.
     */    
    public String getName()
    {
        return name;
    }
    
    /**
     * Mutator method for this instance's name attribute.
     * @param name a string representing a formatted location name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Accessor method for this instance's x attribute.
     * @return the value of the x attribute.
     */    
    public double getX()
    {
        return x;
    }
    
    /**
     * Mutator method for this instance's x attribute.
     * @param x the x coordinate of the location.
     */
    public void setX(double x)
    {
        this.x = x;
    }
    
    /**
     * Accessor method for this instance's y attribute.
     * @return the value of the y attribute.
     */    
    public double getY()
    {
        return y;
    }
    
    /**
     * Mutator method for this instance's y attribute.
     * @param y the y coordinate of the location.
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Accessor method for this instance's srs attribute.
     * @return the value of the srs attribute.
     */    
    public String getSrs()
    {
        return srs;
    }
    /**
     * Mutator method for this instance's srs attribute.
     * @param srs the srs coordinate of the location.
     */
    public void setSrs(String srs)
    {
        this.srs = srs;
    }
    
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        Location that = (Location) o;
        if (!id.equals(that.id))
        {
            return false;
        }
        if (score != that.score)
        {
            return false;
        }
        if (!name.equals(that.name))
        {
            return false;
        }
        if (x != that.x)
        {
            return false;
        }
        if (y != that.y)
        {
            return false;
        }
        if (!srs.equals(that.srs))
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hashCode = 31;
        hashCode *= id.hashCode();
        hashCode *= score;
        hashCode *= name.hashCode();
        hashCode *= x;
        hashCode *= y;
        hashCode *= srs.hashCode();
        return hashCode;
    }

}
