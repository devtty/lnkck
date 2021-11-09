package com.devtty;

import javafx.beans.property.*;

public class UrlCheck{

    private StringProperty urltocheck;
    private IntegerProperty status;

    public void setUrltocheck(String value){
	urltocheckProperty().set(value);
    }

    public String getUrltocheck(){
	return urltocheckProperty().get();
    }
    
    public void setStatus(Integer value){
	statusProperty().set(value);
    }

    public Integer getStatus(){
	return statusProperty().get();
    }

    public StringProperty urltocheckProperty(){
	if(urltocheck == null)
	    urltocheck = new SimpleStringProperty(this, "urltocheck");

	return urltocheck;
    }

    public IntegerProperty statusProperty(){
	if(status == null)
	    status = new SimpleIntegerProperty(this, "status", 0);
	
	return status;
    }
    
    public UrlCheck(String urltocheck){
	setUrltocheck(urltocheck);
    }
    
}
