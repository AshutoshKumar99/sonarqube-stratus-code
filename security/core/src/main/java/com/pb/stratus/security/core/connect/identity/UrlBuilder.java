package com.pb.stratus.security.core.connect.identity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Add query parameters to URL
 */
public class UrlBuilder {
    private final List<QueryParameter> m_parameters = new ArrayList<QueryParameter>();
    private final String m_url;
    private final String m_tenantName;

    UrlBuilder(String tenantName, String urlString) {
        m_url = urlString;
        m_tenantName = tenantName;
    }

    void addQueryParameter(String name, String encodedValue) {
        QueryParameter qp = new QueryParameter(name, encodedValue);
        m_parameters.add(qp);
    }

    String getUrl() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m_parameters.size(); i++) {
            if (i > 0) {
                sb.append("&");
            }
            QueryParameter qp = m_parameters.get(i);
            sb.append(qp.getName());
            if (qp.getEncodedValue() != null) {
                sb.append("=");
                sb.append(qp.getEncodedValue());
            }
        }

        StringBuilder urlBuffer = new StringBuilder(getBody());

        if (m_url.indexOf('?')>=0) {
            urlBuffer.append("&");
        } else if (sb.length() > 0) {
            urlBuffer.append("?");
        }
        urlBuffer.append(sb);
        return urlBuffer.toString();
    }

    private String getBody()
    {
        String url;

        try
        {
            URL from = new URL(m_url);

            URL to = new URL(from.getProtocol(),
                   from.getHost(),
                   from.getPort(),
                   "/" +  m_tenantName + from.getFile());

            url = to.toString();
        }
        catch(java.net.MalformedURLException e) {
            url = "/" + m_tenantName + "/";
        }

        return url;
    }

    public void addQueryStringFragment(String urlQuery) {
        if (urlQuery == null) {
            return;
        }
        String[] nameValuePairs = urlQuery.split("&");
        for (String pair : nameValuePairs) {
            String[] parameter = pair.split("=", 2);
            QueryParameter qp = new QueryParameter();
            qp.setName(parameter[0]);
            if (parameter.length > 1) {
                qp.setEncodedValue(parameter[1]);
            }
            m_parameters.add(qp);
        }
    }

    private static class QueryParameter {
        private String name;
        private String encodedValue;

        public QueryParameter(String name, String encodedValue) {
            setName(name);
            setEncodedValue(encodedValue);
        }

        public QueryParameter() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setEncodedValue(String encodedValue) {
            this.encodedValue = encodedValue;
        }

        public String getEncodedValue() {
            return encodedValue;
        }
    }
}
