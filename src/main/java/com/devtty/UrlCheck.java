package com.devtty;

import javafx.beans.property.*;

public class UrlCheck{

    private StringProperty urltocheck;

    public void setUrltocheck(String value){
	urltocheckProperty().set(value);
    }

    public String getUrltocheck(){
	return urltocheckProperty().get();
    }

    public StringProperty urltocheckProperty(){
	if(urltocheck == null)
	    urltocheck = new SimpleStringProperty(this, "urltocheck");

	return urltocheck;
    }

    public UrlCheck(String urltocheck){
	setUrltocheck(urltocheck);
    }
    
}
