package com.pb.custom.lucene;

import com.pb.gazetteer.search.QueryUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/17/13
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public final class CustomFilter extends TokenFilter {

    private static final Logger logger = LogManager.getLogger(CustomFilter.class);

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private static final String WORD_TYPE = TypeAttribute.DEFAULT_TYPE;

    private final static char COMMA = ',';
    private final static String HYPHEN = "-";

    /**
     * Construct a token stream filtering the given input.
     */
    public CustomFilter(TokenStream input) {
        super(input);
    }

    /**
     * I will generate tokens for the information I parse in this structure.
     */
    public List<String> extraElements = new LinkedList<String>();

    private State stateSaved;

    private int containsHyphen(String token) {
        if (token != null)
            return token.indexOf(HYPHEN);
        return -1;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!extraElements.isEmpty()) {
            restoreState(stateSaved);
            termAtt.setEmpty().append(extraElements.remove(0));
            return true;
        }

        if (input.incrementToken()) {
            final char[] buffer = termAtt.buffer();
            final int bufferLength = termAtt.length();
            final String type = typeAtt.type();

            if (type.equals(WORD_TYPE) &&      // ,
                    buffer[bufferLength - 1] == COMMA) {
                extraElements.add(QueryUtils.stripCommas(new String(buffer, 0, bufferLength)).trim());
                stateSaved = captureState();
            }
            if (type.equals(WORD_TYPE) &&      // ,
                    bufferLength > 2 && (containsHyphen(new String(buffer, 0, bufferLength)) >= 0)) {
                generateTokens(new String(buffer, 0, bufferLength));
                stateSaved = captureState();
            }

            return true;
        }
        return false;
    }

    private void generateTokens(String hyphenatedString) {

        if (hyphenatedString != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(hyphenatedString);
            while (stringTokenizer.hasMoreElements()) {
                extraElements.add(stringTokenizer.nextToken(HYPHEN).trim());
            }
        }
    }
}
