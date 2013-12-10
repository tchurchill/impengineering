/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracletoxtencil.model;
import java.util.ArrayList;

/**
 *
 * @author mburris
 */
public class Group {
    
    private String groupName;
    private String groupJavaName;
    private String minRepetitions;
    private String maxRepetitions;
    private ArrayList<Field> fields;
    private String code;
    private String qualifier;

    public Group(String groupName, String groupJavaName ,String minRepetitions, String maxRepetitions, String code, String qualifier) {
        this.groupName = groupName;
        this.groupJavaName = groupJavaName;
        this.minRepetitions = minRepetitions;
        this.maxRepetitions = maxRepetitions;
        this.code = code;
        this.qualifier = qualifier;
        this.fields = new ArrayList<Field>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMaxRepetitions() {
        return maxRepetitions;
    }

    public void setMaxRepetitions(String maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    public String getMinRepetitions() {
        return minRepetitions;
    }

    public void setMinRepetitions(String minRepetitions) {
        this.minRepetitions = minRepetitions;
    }

    public String getGroupJavaName() {
        return groupJavaName;
    }

    public void setGroupJavaName(String groupJavaName) {
        this.groupJavaName = groupJavaName;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field){
        this.fields.add(field);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }
    
}
