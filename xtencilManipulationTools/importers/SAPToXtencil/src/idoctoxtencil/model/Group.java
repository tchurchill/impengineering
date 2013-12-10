package idoctoxtencil.model;

public class Group {
	
	private String groupName;
        private String givenGroupName;


	private String groupJavaName;
	
	private String minRepetitions;
	private String maxRepetitions;
	
	private Boolean required;
	
	private Group[] childGroups;
	private int numberOfChildGroups = 0;

	private Field[] fields;

	
	
	public Group() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName , String prefix) {
                /*
                if(groupName.contains("/")){
                    groupName = groupName.replaceAll("/", "");
                }*/
             
                setGivenGroupName(groupName);
		
                if(!groupName.startsWith("Z")){
                    this.groupName = prefix+groupName.substring(2);
                }else{
                    if(prefix.equals("E1")){
                        this.groupName = "Z1"+groupName.substring(2);
                    }else if(prefix.equals("E2")){
                        this.groupName = "Z2"+groupName.substring(2);
                    }
                }
                
                
                setGroupJavaName(this.groupName);
	}

        public String getGivenGroupName() {
            return givenGroupName;
        }

        public void setGivenGroupName(String givenGroupName) {
            this.givenGroupName = givenGroupName;
        }
	
	void setGroupJavaName(String groupName){
		if(groupName != null){
			this.groupJavaName = Character.toString(Character.toLowerCase(groupName.charAt(0)))+groupName.substring(1,groupName.length());
		}
	}
	
	public String getGroupJavaName(){
		return groupJavaName;
	}

	public String getMinRepetitions() {
		return minRepetitions;
	}

	public void setMinRepetitions(String minRepetitions) {
		this.minRepetitions = minRepetitions;
	}

	public String getMaxRepetitions() {
		return maxRepetitions;
	}

	public void setMaxRepetitions(String maxRepetitions) {
		String newVal = "";		
		if(maxRepetitions.length() > 9){
			newVal = maxRepetitions.substring(0,9);
		}else{
			newVal = maxRepetitions;
		}
		this.maxRepetitions = newVal;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	public void setchildGroups(Group[] childGroups) {
		this.childGroups = childGroups;
	}

	public Group[] getchildGroups() {
		return childGroups;
	}
	
	//Add a Child Group
	public void addChildGroup(Group childGroup){
		//Create Temp Group
		Group[] tempGroups = new Group[childGroups.length+1];
		//Copy items from childGroups
		System.arraycopy(childGroups, 0, tempGroups, 0, childGroups.length);
		//Add new childGroup to tempGroups
		tempGroups[numberOfChildGroups]= childGroup;
		//Reset childGroups to tempGroups length
		childGroups = new Group[tempGroups.length];
		//Copy items from tempGroups to childGroups
		System.arraycopy(tempGroups, 0, childGroups, 0, tempGroups.length);
		//Increment numberOfChildGroups counter
		numberOfChildGroups++;
	}
	
	
}
