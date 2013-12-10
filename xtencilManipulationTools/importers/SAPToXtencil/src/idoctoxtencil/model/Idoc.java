package idoctoxtencil.model;

public class Idoc {
	
	private String	iDocType;
	private Group[] iDocGroups;
	
	private Group[] childGroups;
	private int numberOfChildGroups = 0;
	
	
	public Idoc() {
		super();
	}
	
	public String getiDocType() {
		return iDocType;
	}

	public void setiDocType(String iDocType) {
		this.iDocType = iDocType;
	}

	public Group[] getiDocGroups() {
		return iDocGroups;
	}

	public void setiDocGroups(Group[] iDocGroups) {
		this.iDocGroups = iDocGroups;
	}

	public Group[] getChildGroups() {
		return childGroups;
	}

	public void setChildGroups(Group[] childGroups) {
		this.childGroups = childGroups;
	}


	public int getNumberOfChildGroups() {
		return numberOfChildGroups;
	}

	public void setNumberOfChildGroups(int numberOfChildGroups) {
		this.numberOfChildGroups = numberOfChildGroups;
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
