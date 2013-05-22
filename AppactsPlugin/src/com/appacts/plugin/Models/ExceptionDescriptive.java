package com.appacts.plugin.Models;

@SuppressWarnings("serial")
public class ExceptionDescriptive extends Exception {
    
    public final String StackTrace;
    public final String Data;
    public final String Source;
    
    public ExceptionDescriptive(String message) {    
        super(message);
        
        this.StackTrace = "";
        this.Data = "";
        this.Source = "";
    }
    
    public ExceptionDescriptive(Exception ex) {  
        super(ex.getMessage());
        
        this.Source = ex.toString();
        this.StackTrace = "";
        this.Data = "";
    }
    
    public ExceptionDescriptive(Exception ex, String data) {  
        super(ex.getMessage());

        this.Source = ex.toString();
        this.Data = data;
        this.StackTrace = "";
    }

    public ExceptionDescriptive(String message, String stackTrace, String source, String data) {    
        super(message);
        
        this.StackTrace = stackTrace;
        this.Source = source;
        this.Data = data;
    }
    
    /*
    this is supported only in latest versions
    public ExceptionDescriptive(String message, StackTraceElement[] stackTrace) {    
        super(message);
        
      
        for(int i = 0; i < stackTrace.length; i++) {
            this.StackTrace += stackTrace[i].toString();
            this.Source +=  
            "Exception thrown from " + stackTrace[i].getMethodName()  
            + " in class " +  stackTrace[i].getClassName() + " [on line number "  
            +  stackTrace[i].getLineNumber() + " of file " + stackTrace[i].getFileName() + "]";
        } 
    }
        public ExceptionDescriptive(String message, StackTraceElement[] stackTrace, String data) {    
        super(message);
        
        this.Data = data;
     
        for(int i = 0; i < stackTrace.length; i++) {
            this.StackTrace += stackTrace[i].toString();
            this.Source +=  
            "Exception thrown from " + stackTrace[i].getMethodName()  
            + " in class " +  stackTrace[i].getClassName() + " [on line number "  
            +  stackTrace[i].getLineNumber() + " of file " + stackTrace[i].getFileName() + "]";
        } 
    } */    
} 