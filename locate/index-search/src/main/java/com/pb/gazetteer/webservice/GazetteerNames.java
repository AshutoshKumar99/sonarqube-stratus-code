package com.pb.gazetteer.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class GazetteerNames {

	@XmlElement(required = true)
	private String defaultGazetteerName;
	@XmlElement(required = false)
	private List<GazetteerInstance> gazetteerInstances;
	public String getDefaultGazetteerName() {
		return defaultGazetteerName;
	}
	public void setDefaultGazetteerName(String defaultGazetteerName) {
		this.defaultGazetteerName = defaultGazetteerName;
	}
	public List<GazetteerInstance> getGazetteerInstances() {
		if(gazetteerInstances==null)
		{
			gazetteerInstances=new ArrayList<GazetteerInstance>();
	}
		return gazetteerInstances;
	}
}
