package com.pb.gazetteer;

/**
 * An address is a set of information to describe a property, or similar
 * structure, mainly for the purpose of determining its physical location.
 * The logical location represented as a string returned by
 * {@link #getAddress()} is also accompanied by a point given by the x, y, and
 * srs properties. That point is, for most applications, a sufficient
 * approximation of the property represented by this address
 */
public class Address {
    private String id;
    private String address;
    private double x;
    private double y;
    private String srs;

    /**
     * every address searched has an associated score with it, which needs to be filled.
     */
    private float score;

    /*
     * default Constructor
     */
    public Address() {
      //nothing
    }

    public Address(String id, String address, double x, double y, String srs) {
        this.id = id;
        this.address = address;
        this.x = x;
        this.y = y;
        this.srs = srs;
        this.score = 0;
    }

    public Address(String id, String address, double x, double y, String srs, float score) {
        this.id = id;
        this.address = address;
        this.x = x;
        this.y = y;
        this.srs = srs;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Address)) return false;

        Address that = (Address) obj;
        return this.id.equals(that.getId()) &&
                this.address.equals(that.address) &&
                this.x == that.x &&
                this.y == that.y &&
                this.srs == that.srs;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + id.hashCode();
        result = 37 * result + (int) x;
        result = 37 * result + (int) y;
        result = 37 * result + address.hashCode();
        result = 37 * result + srs.hashCode();
        return result;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the srs
     */
    public String getSrs() {
        return srs;
    }

    /**
     * @param srs the srs to set
     */
    public void setSrs(String srs) {
        this.srs = srs;
    }

    /**
     * Returns the associated score.
     *
     * @return
     */
    public float getScore() {
        return score;
    }

    /**
     * Set the score of the address found.
     *
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /*
     * prints the indexed address
     */
    public String toString() {

        return "id: " + this.id + ",  address: " + this.address + " score:" + this.score;

    }

}
