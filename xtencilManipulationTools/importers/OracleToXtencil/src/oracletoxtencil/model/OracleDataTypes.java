/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracletoxtencil.model;

import java.util.HashMap;

/**
 *
 * @author mburris
 */
public class OracleDataTypes {
    
    private HashMap datatypes;

    public OracleDataTypes() {
        this.datatypes = new HashMap();
        this.datatypes.put("VARCHAR2","STRING");
        this.datatypes.put("DATE","DATE");
        this.datatypes.put("NUMBER","NUMBER");
    }
    
    public String getDatetype(String key){
        String returnValue = "";
        if(this.datatypes.containsKey(key)){
            returnValue = (String)this.datatypes.get(key);
        }
        return returnValue;
    }
    
    public Boolean hasDatatype(String key){
        Boolean returnValue = false;
        if(this.datatypes.containsKey(key)){
            returnValue = true;
        }
        return returnValue;
    }
    
    
}
