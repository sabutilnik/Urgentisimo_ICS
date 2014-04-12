package uno.Urgentisimo;

import java.util.ArrayList;
 
public class Parent {
    private String mTitle;
    private String mDesc;
    private String elid;
    private ArrayList<String> mArrayChildren;
    private ArrayList<String> losid;
 
    public String getTitle() {
        return mTitle;
    }
    public String getDesc() {
    	return mDesc;
    }
 
    public void setTitle(String mTitle,String mDesc) {
        this.mTitle = mTitle;
        this.mDesc = mDesc;
    }
    
    public void  setid(String elid) {
    	this.elid = elid;
    }
    
    public String getid() {
    	return this.elid;
    }
    public ArrayList<String> getArrayChildren() {
        return mArrayChildren;
    }
    public ArrayList<String> getlosid() {
    	return losid;
    }
 
    public void setArrayChildren(ArrayList<String> mArrayChildren) {
        this.mArrayChildren = mArrayChildren;
    }
    public void setlosid(ArrayList<String> mlosid) {
    	this.losid = mlosid;
    }
}