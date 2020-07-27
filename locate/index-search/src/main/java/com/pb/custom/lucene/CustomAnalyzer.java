package com.pb.custom.lucene;

import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.*;

import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/5/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Our custom analyzer that can be upgraded to accommodate various cases.
 */
public class CustomAnalyzer extends Analyzer {
    private final static Version VERSION = Version.LUCENE_45;
    private final static String THE = "the";

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        /**
         * The StandardTokenizer was removing the special characters which are required to be indexed,
         * so switching to whitespace tokenizer for the time being.
         */
        final WhitespaceTokenizer src = new WhitespaceTokenizer(VERSION, reader);

        /**
         * Modifying the filters to ensure I get the required TermVectors.
         */
        TokenStream tok = new LowerCaseFilter(VERSION, src);//new StandardFilter(VERSION, src);

        //CONN-32818: Remove 'the' from stopwords.
        Iterator iterator = StopAnalyzer.ENGLISH_STOP_WORDS_SET.iterator();
        Set stopWords = new HashSet<>();
        while (iterator.hasNext()) {
            Object word = iterator.next();
            if (word instanceof char[]) {
                String stopWord = String.valueOf((char[]) word);
                if (stopWord.equals(THE)) {
                    continue;
                }
                stopWords.add(stopWord);
            }
        }

        CharArraySet stopWordsSet = new CharArraySet(Version.LUCENE_CURRENT,
                stopWords, false);
        stopWordsSet = CharArraySet.unmodifiableSet(stopWordsSet);

        tok = new StopFilter(VERSION, tok, stopWordsSet);
        tok = new StandardFilter(VERSION, tok);
        /**
         * Specifically takes care of ','
         */
        tok = new CustomFilter(tok);

        return new TokenStreamComponents(src, tok);
    }
}
