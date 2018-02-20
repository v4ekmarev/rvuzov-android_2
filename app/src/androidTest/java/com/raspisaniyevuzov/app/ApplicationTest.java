package com.raspisaniyevuzov.app;

import android.test.ApplicationTestCase;

import com.raspisaniyevuzov.app.db.dao.UniversityDAO;
import com.raspisaniyevuzov.app.db.model.University;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<RVuzovApp> {

    private RVuzovApp mApplication;

    public ApplicationTest() {
        super(RVuzovApp.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        mApplication = getApplication();
        mApplication.onCreate();
    }

    public void testCreateUniversity() {
        String uName = "University Name";
        // for test
        UniversityDAO universityDao = UniversityDAO.getInstance();
        University university = new University();
        String id = UUID.randomUUID().toString();
        university.setId(id);
        university.setName(uName);
        universityDao.save(university);
        University u = (University) universityDao.get(id, University.class);

        assertEquals(uName, u.getName());
    }

}