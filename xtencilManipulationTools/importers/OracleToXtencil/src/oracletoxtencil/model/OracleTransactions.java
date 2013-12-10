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
public class OracleTransactions {
    
    private HashMap transactions;
    
    public OracleTransactions() {
        this.transactions = new HashMap();
        this.transactions.put("INO","810|OUTBOUND");
        this.transactions.put("POI","850|INBOUND");
        this.transactions.put("POCI","860|INBOUND");
        this.transactions.put("DSNO","856|OUTBOUND");

    }
    
    public String getTransaction(String key){
        String returnValue = "";
        if(this.transactions.containsKey(key)){
            returnValue = (String)this.transactions.get(key);
        }
        return returnValue;
    }
    
    public Boolean hasTransaction(String key){
        Boolean returnValue = false;
        if(this.transactions.containsKey(key)){
            returnValue = true;
        }
        return returnValue;
    }
    
}
