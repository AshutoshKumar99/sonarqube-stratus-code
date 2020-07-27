/**
 * 
 */
package com.pb.gazetteer.webservice;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author SI001JY
 * Will contain details of each Gazetter instance, like its name, Projection or search Logic
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GazetteerInstance {
	@XmlElement(required = true)
	private String gazetteerName;
	@XmlElement(required = false)
	private String  engineName;
	@XmlElement(required = true)
	private String srs;

    public String getGazetteerName() {
		return gazetteerName;
	}
	public void setGazetteerName(String gazetteerName) {
		this.gazetteerName = gazetteerName;
	}
	public String getEngineName() {
		return engineName;
	}
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	public String getSrs() {
		return srs;
	}
	public void setSrs(String srs) {
		this.srs = srs;
	}

	public boolean equals(Object o){
		if(!(o instanceof GazetteerInstance))
		return false;
		if(((GazetteerInstance)o).getGazetteerName().equals(gazetteerName))
			return true;
		return false;
	}
	public int hashCode(){
		return this.gazetteerName.hashCode();
	}
}
