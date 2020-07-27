package com.pb.stratus.security.core.resourceauthorization;

import com.pb.stratus.core.common.Preconditions;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 12:53 PM
 * Default SAX parser for parsing the auth files.
 */
public class DefaultResourceParserImpl implements ResourceParser {

    private static final String PREFIX = "ROLE_";
    private static final String AUTHORIZED_ROLE_ELEMENT_NAME = "AuthorizedRole";

    /**
     * Parse the xml stream and also convert the exceptions into ResourceException
     *
     * @param is InputStream
     * @return List<GrantedAuthority>
     * @throws ResourceException
     */
    @Override
    public List<GrantedAuthority> parse(InputStream is) throws ResourceException {
        Preconditions.checkNotNull(is, "InputStream cannot be null");
        SAXParserFactory spf = SAXParserFactory.newInstance();
        List<GrantedAuthority> grantedAuthorities = null;
        try {
            SAXParser sp = spf.newSAXParser();
            ResourceAuthzParser resAuthzParser = new ResourceAuthzParser();
            sp.parse(is, resAuthzParser);
            grantedAuthorities = resAuthzParser.getGrantedAuthorities();
        } catch (ParserConfigurationException e) {
            throw new ResourceException(e);
        } catch (SAXException e) {
            throw new ResourceException(e);
        } catch (IOException e) {
            throw new ResourceException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new ResourceException(e);
            }
        }
        return grantedAuthorities;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    private static class ResourceAuthzParser extends DefaultHandler {

        private List<GrantedAuthority> grantedAuthorities;
        private GrantedAuthority grantedAuthority;
        private StringBuilder tempVal;

        public ResourceAuthzParser() {
            tempVal = new StringBuilder();
            grantedAuthorities = new ArrayList<GrantedAuthority>();
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            tempVal.append(ch, start, length);

        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            //reset
            tempVal.setLength(0);
        }

        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
            if (qName.equalsIgnoreCase(AUTHORIZED_ROLE_ELEMENT_NAME)) {
                String role = tempVal.toString();
                // gdutt: CONN-15452-Allow custom roles
                // Auth file shall have these role names :Administrators,  Users, Public. Any other name shall be treated
                // as a custom role
                //TODO:  Below if  statement is for backward  compatibility with  R36.  it shall be removed after R37
                if (AuthorizationRoles.ADMINISTRATOR.getRoleName().equals(role) || AuthorizationRoles.USER.getRoleName().equals(role)) {
                    role = role + "s";
                }
                grantedAuthority = new GrantedAuthorityImpl(addPrefix(role));
                grantedAuthorities.add(grantedAuthority);
            }
        }

        private String addPrefix(String role) {
            return PREFIX + role;
        }

        public List<GrantedAuthority> getGrantedAuthorities() {
            return grantedAuthorities;
        }
    }
}

