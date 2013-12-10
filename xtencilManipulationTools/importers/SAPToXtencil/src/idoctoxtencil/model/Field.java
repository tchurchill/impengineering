package idoctoxtencil.model;

public class Field {
	
	private String fieldName;
	private String fieldJavaName;
	
	private String minLength;
	private String maxLength;
	
	private boolean required;

	public Field() {
		super();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		setFieldJavaName(fieldName);
	}
	
	void setFieldJavaName(String fieldName){
		if(fieldName != null){

                    if(fieldName.contains("/")){
                        fieldName = fieldName.replaceAll("/", "");
                    }
                    this.fieldJavaName = Character.toString(Character.toLowerCase(fieldName.charAt(0)))+fieldName.substring(1,fieldName.length());
		}
	}
	
	public String getFieldJavaName(){
		return fieldJavaName;
	}
	
	public String getMinLength() {
		return minLength;
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	

}
