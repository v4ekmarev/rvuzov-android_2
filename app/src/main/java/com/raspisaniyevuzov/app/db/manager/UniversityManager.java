package com.raspisaniyevuzov.app.db.manager;

import com.raspisaniyevuzov.app.db.dao.UniversityDAO;
import com.raspisaniyevuzov.app.db.model.University;

import java.util.Date;
import java.util.List;

/**
 * Created by SAPOZHKOV on 18.09.2015.
 */
public class UniversityManager {

    public static void createUniversity(String name, String abbr, String apitocken, String contact, Date dateUpdate, String syncUrl, String timeZone, String website, boolean isActive) {
        University university = new University();
//        university.setId(); // TODO set id
        university.setName(name);
        university.setAbbr(abbr);
        university.setApitoken(apitocken);
        university.setContact(contact);
        university.setDateUpdate(dateUpdate);
        university.setSyncUrl(syncUrl);
        university.setTimezone(timeZone);
        university.setWebsite(website);
        university.setIsActive(isActive);

//        UniversityDAO.getInstance(realm).createUniversity(university);
    }

    public static University getUniversity() {
        University university = null;
        List<University> universities = UniversityDAO.getInstance().getAll(University.class);
        if (!universities.isEmpty())
            university = universities.get(0);
        return university;
    }

}
