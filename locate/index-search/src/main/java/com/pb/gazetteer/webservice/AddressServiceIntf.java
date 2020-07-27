package com.pb.gazetteer.webservice;

import com.pb.gazetteer.Address;
import com.pb.gazetteer.PopulateParameters;
import com.pb.gazetteer.PopulateResponse;
import com.pb.gazetteer.SearchParameters;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlMimeType;
import java.rmi.ServerException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/11/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AddressServiceIntf {

    @WebMethod
    public List<Address> search(
            @WebParam(name = "searchParameters") SearchParameters searchParameters)
            throws ServerException, LocateException;

    @WebMethod()
    public GazetteerNames getGazetteerNames(
            @WebParam(name = "tenantName") String tenantName)
            throws ServerException, LocateException;

    @WebMethod()
    public PopulateResponse populateGazetteer(
            @WebParam(name = "populateParameters") PopulateParameters populateParameters,
            @XmlMimeType("application/octet-stream") @WebParam(name = "data") DataHandler data)
            throws ServerException;
}
