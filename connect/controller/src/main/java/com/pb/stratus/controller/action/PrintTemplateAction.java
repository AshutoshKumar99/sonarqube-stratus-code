package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.print.TemplateRepository;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 6/27/13
 * Time: 12:25 PM
 * Responsible for retrieving any kind of information from the FO print template.
 */
public class PrintTemplateAction extends DataInterchangeFormatControllerAction {

    private TemplateRepository repo = null;
    private static final Logger logger = LogManager
            .getLogger(PrintTemplateAction.class);

    private static String MAP_ATTR = "stratus:map";
    private static String MAP_HEIGHT = "height";
    private static String MAP_WIDTH = "width";

    public PrintTemplateAction(TemplateRepository repo) {
        this.repo = repo;
    }

    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        String templateName = request.getParameter(TEMPLATE_NAME_PARAM);
        Document doc = null;
        JSONObject json = null;
        try {
            doc = repo.parseFoFile(templateName);
            json = getElementForWidthHeight(doc);
        }
        catch (Exception e) {
            logger.debug(e);
            throw new ServletException(e);
        }

        return json;
    }

    /**
     * Gets the width and height by parsing the content of a tag called stratus:map.
     *
     * @param doc - the parsed Fo file.
     * @return json object - map of height and width with their values.
     */
    private JSONObject getElementForWidthHeight(Document doc) throws Exception {
        JSONObject json = new JSONObject();
        NodeList list = doc.getElementsByTagName(MAP_ATTR);
        if (list!= null) {
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                NamedNodeMap attributes = element.getAttributes();

                Attr h = (Attr) attributes.getNamedItem(MAP_HEIGHT);
                if(h == null) throw new Exception();
                Attr w = (Attr) attributes.getNamedItem(MAP_WIDTH);
                if(w == null) throw new Exception();
                json.put("h", h.getValue());
                json.put("w", w.getValue());
            }
        }
        else
            throw new Exception();


        return json;
    }

}
