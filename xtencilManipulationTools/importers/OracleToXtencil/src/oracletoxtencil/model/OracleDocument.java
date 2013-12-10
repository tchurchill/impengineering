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
public class OracleDocument {
    
    private String docType;
    private String direction;
    private HashMap groups;
    private int detailGroupStart;

    public OracleDocument(String docType, String direction) {
        this.docType = docType;
        this.direction = direction;
        this.groups = new HashMap();
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public void addGroup(Group group){
        this.groups.put(group.getGroupName(),group);
    }
    
    public Boolean hasGroup(String group){
        Boolean returnValue = false;
        if(this.groups.containsKey(group)){
            returnValue = true;
        }
        return returnValue;
    }
    
    public Group getGroup(String group){
        Group returnValue = null;
        if(this.hasGroup(group)){
            returnValue = (Group)this.groups.get(group);
        }
        return returnValue;
    }

    public HashMap getGroups() {
        return groups;
    }
    
    
}
