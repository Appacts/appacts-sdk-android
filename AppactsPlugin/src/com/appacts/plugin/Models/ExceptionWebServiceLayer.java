package com.appacts.plugin.Models;

@SuppressWarnings("serial")
public final class ExceptionWebServiceLayer extends ExceptionDescriptive {
    
	public ExceptionWebServiceLayer(Exception ex) {    
        super(ex);
    }
    
    public String toString() {
        return "ExceptionWebServiceLayer: " + super.toString();
    }
    
} 