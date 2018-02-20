package com.raspisaniyevuzov.app.db.dbimport;

import com.raspisaniyevuzov.app.misc.scheduleclasses.SortableScheduleClass;

import java.io.Serializable;

public class ScheduleCatalog extends SortableScheduleClass implements Serializable {

    private static final long serialVersionUID = 2238965957772664828L;
    
    public static final String DB_ID = "_id";
    public static final String DB_DEPARTMENT_ID = "department_id";
    public static final String DB_NAME = "name";
    public static final String DB_OCCUPATION = "occupation";
    public static final String DB_EMAILS = "emails";
    public static final String DB_INT_PHONES = "int_phones";
    public static final String DB_EXT_PHONES = "ext_phones";
    public static final String DB_URLS = "urls";
    public static final String DB_LOCATIONS = "locations";
    
    public ScheduleCatalog(){}

    public ScheduleCatalog(String departmentId, String id, String occupation, String name, String[] emails,
            String[] phonesInt, String[] phonesExt, String[] urls, String[] locations) {
        super.setId(id);
        super.setName(name);
        this.departmentId = departmentId;
        this.occupation = occupation;
        this.emails = emails;
        this.phonesInt = phonesInt;
        this.phonesExt = phonesExt;
        this.urls = urls;
        this.locations = locations;

    }
    
    private String departmentId;
    private String occupation;
    private String[] emails;
    private String[] phonesInt;
    private String[] phonesExt;
    private String[] urls;
    private String[] locations;
    
    public String getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(String id){
        this.departmentId = id;
    }
    
    public String getOccupation(){
        return occupation;
    }
    
    public void setOccupation(String occupation){
        this.occupation = occupation;
    }
    
    public String[] getEmails(){
        return emails;
    }
    
    public void setEmails(String[] emails){
        this.emails = emails;
    }
    
    public String[] getIntPhones(){
        return phonesInt;
    }
    
    public void setIntPhones(String[] phones){
        this.phonesInt = phones;
    }
    
    public String[] getExtPhones(){
        return phonesExt;
    }
    
    public void setExtPhones(String[] phones){
        this.phonesExt = phones;
    }
    
    public String[] getUrls(){
        return urls;
    }
    
    public void setUrls(String[] urls){
        this.urls = urls;
    }
    
    public String[] getLocations(){
        return locations;
    }
    
    public void setLocations(String[] locations){
        this.locations = locations;
    }

    @Override
    public String toString() {
        String result = occupation + "\n";
        if (getName() != null) {
            result += getName() + "\n";
        }
        result += fieldsForString(emails);
        result += fieldsForString(phonesInt);
        result += fieldsForString(phonesExt);
        result += fieldsForString(urls);
        result += fieldsForString(locations);
        return result;
    }
    
    private String fieldsForString(String [] array){
        String result = "";
        if (array != null) {
            for(String item : array) {
                result += item + "\n";
            }
        }
        return result;
    }
}