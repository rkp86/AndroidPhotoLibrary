package com.example.photolibrary.model;

import java.io.Serializable;


public class Tag implements Serializable{


    private static final long serialVersionUID = -4411233571260876183L;
    private String tagName;


    private String tagValue;


    public Tag() {
        tagName = null;
        tagValue = null;
    }


    public Tag(String n, String v) {
        tagName = n;
        tagValue = v;
    }

    public String getName() {
        return tagName;
    }


    public String getValue() {
        return tagValue;
    }

    public String toString() {
        return tagName + "=" + tagValue;
    }

}
