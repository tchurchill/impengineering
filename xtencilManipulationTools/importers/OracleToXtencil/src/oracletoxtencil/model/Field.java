/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracletoxtencil.model;

/**
 *
 * @author mburris
 */
public class Field {
    
    private String fieldName;
    private String fieldJavaName;
    private String minimumLength;
    private String maximumLength;
    private String dataType;

    public Field(String fieldName,String minimumLength, String maximumLength, String dataType) {
        this.fieldName = fieldName;
        //this.fieldJavaName = fieldJavaName;
        String jNameLower = Character.toString(Character.toLowerCase(fieldName.charAt(0)))+fieldName.substring(1,fieldName.length());
        String jNoSpecial = jNameLower.replaceAll("[\\W]", "");
        this.fieldJavaName = jNoSpecial;
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldJavaName() {
        return fieldJavaName;
    }

    public void setFieldJavaName(String fieldJavaName) {
        this.fieldJavaName = fieldJavaName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMaximumLength() {
        return maximumLength;
    }

    public void setMaximumLength(String maximumLength) {
        this.maximumLength = maximumLength;
    }

    public String getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(String minimumLength) {
        this.minimumLength = minimumLength;
    }
    
}
