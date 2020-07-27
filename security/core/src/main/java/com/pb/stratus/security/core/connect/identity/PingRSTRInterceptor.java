package com.pb.stratus.security.core.connect.identity;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.util.Iterator;

/**
 * Checks that any incoming RequestSecurityTokenResponses that include SAML Assertions also include corresponding
 * RequestedAttachedReference elements, and adds the RequestedAttachedReference where it is missing. This interceptor corrects an apparent defect in the PingFederate 6.4.1
 * Security Token Service, in that the PF STS omits the RequestedAttachedReference element when it should be including it
 * according to the WS-Trust 1.3 specification (when describing the RequestedAttachedReference element, the WS-Trust
 * specification initially notes that it is optional, but then later indicates that it is in fact REQUIRED for those Security Tokens
 * types -- of which SAML Assertions are one -- that cannot be directly labeled with a wsu:Id attribute.
 */
public class PingRSTRInterceptor extends AbstractPhaseInterceptor {

    private static String WSTRUST_NS = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
    private static String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    public PingRSTRInterceptor() {
        super(Phase.PRE_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix.equals("wst")) {
                    return WSTRUST_NS;
                }

                return null;
            }

            public Iterator getPrefixes(String val) {
                return null;
            }

            public String getPrefix(String uri) {
                return "wst";
            }
        };

        try {
            xpath.setNamespaceContext(ctx);
            XPathExpression expr = xpath.compile("//wst:RequestSecurityTokenResponse");
            Object result = expr.evaluate(message.getContent(org.w3c.dom.Node.class), XPathConstants.NODESET);

            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                fixPingRSTR(nodes.item(i));
            }
        }
        catch (XPathExpressionException e) {
            return;
        }
    }

    private void fixPingRSTR(Node rstrNode) throws XPathExpressionException {
        //does the rstrNode contain a saml assertion? If so, then verify that a RequestedAttachedReference exists... otherwise add one.
        //get the token type.
        Node requestedAttachedReferenceNode = findChild(rstrNode, "RequestedAttachedReference");

        if (requestedAttachedReferenceNode != null) {
            return;
        }

        Node tokenTypeNode = findChild(rstrNode, "TokenType");
        String tokenType = tokenTypeNode.getTextContent();

        if (tokenType.contains("SAMLV1")) {
            addReferenceForSAML1xToken(rstrNode);
        } else if (tokenType.contains("SAMLV2")) {
            addReferenceForSAML2xToken(rstrNode);
        }
    }

    private void addReferenceForSAML1xToken(Node rstrNode) {
        Node assertionNode = findSAMLAssertionNode(rstrNode);
        String assertionId = assertionNode.getAttributes().getNamedItem("AssertionID").getNodeValue();
        addRequestedAttachedReferenceNode(rstrNode, assertionId);
    }

    private void addReferenceForSAML2xToken(Node rstrNode) {
        Node assertionNode = findSAMLAssertionNode(rstrNode);
        String assertionId = assertionNode.getAttributes().getNamedItem("ID").getNodeValue();
        addRequestedAttachedReferenceNode(rstrNode, assertionId);
    }

    private Node findSAMLAssertionNode(Node rstrNode) {
        Node requestedToken = findChild(rstrNode, "RequestedSecurityToken");

        return findChild(requestedToken, "Assertion");
    }

    private void addRequestedAttachedReferenceNode(Node rstrNode, String assertionId) {
        Document ownerDoc = rstrNode.getOwnerDocument();
        Element rarElement = ownerDoc.createElementNS(WSTRUST_NS, "RequestedAttachedReference");
        Element strElement = ownerDoc.createElementNS(WSSE_NS, "SecurityTokenReference");
        Element refElement = ownerDoc.createElementNS(WSSE_NS, "Reference");
        refElement.setAttributeNS(WSSE_NS, "URI", assertionId);
        strElement.appendChild(refElement);
        rarElement.appendChild(strElement);
        rstrNode.appendChild(rarElement);
    }

    private Node findChild(Node parent, String childLocalName) {
        NodeList children = parent.getChildNodes();
        Node found = null;

        for (int i = 0; i < children.getLength() && found == null; i++) {
            if (children.item(i).getLocalName().equalsIgnoreCase(childLocalName)) {
                found = children.item(i);
            }
        }

        return found;
    }
}
