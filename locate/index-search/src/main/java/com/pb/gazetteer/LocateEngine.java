package com.pb.gazetteer;

/**
 * An engine is an abstract representation of a type of service that can search
 * for addresses based on partial address strings.
 */
public class LocateEngine {
    private String name;
    private String className;
    private int idlePeriod;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setIdlePeriod(int idlePeriod) {
        this.idlePeriod = idlePeriod;
    }

    public int getIdlePeriod() {
        return idlePeriod;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LocateEngine)) {
            return false;
        }

        LocateEngine locateEngine = (LocateEngine) obj;
        return name.equals(locateEngine.getName());
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

}