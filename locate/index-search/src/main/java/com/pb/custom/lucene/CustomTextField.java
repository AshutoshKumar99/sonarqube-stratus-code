package com.pb.custom.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Saurabh Sharma
 * Date: 12/5/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * The default TextField does not stores the term vectors and hence defining my own.
 */
public class CustomTextField extends Field {
    /* Indexed, tokenized, not stored. */
    public static final FieldType TYPE_NOT_STORED = new FieldType();

    /* Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.setStoreTermVectors(true);
        TYPE_NOT_STORED.setStoreTermVectorPositions(true);
        TYPE_NOT_STORED.freeze();

        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }


    /**
     * Creates a new TextField with Reader value.
     */
    public CustomTextField(String name, Reader reader, Store store) {
        super(name, reader, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    /**
     * Creates a new TextField with String value.
     */
    public CustomTextField(String name, String value, Store store) {
        super(name, value, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    /**
     * Creates a new un-stored TextField with TokenStream value.
     */
    public CustomTextField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }
}
