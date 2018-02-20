package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.Subject;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class SubjectDao extends BaseDAO {

    public SubjectDao() {
        super();
    }

    public static SubjectDao getInstance() {
        return new SubjectDao();
    }

    /**
     * If length equals 1 then use mask "s*", and "*ss*" otherwise
     *
     * @param query - search text
     * @return List of Subjects
     */
    public List<Subject> getByQuery(String query) {
        List<Subject> results = new ArrayList<>();
        if (query != null) {
            RealmResults<Subject> realmResults = getAll(Subject.class);
            // Realm methods beginsWith() and contains() with caseSensitive option work ONLY for English locale
            // Therefore we have to use simple iteration
            for (Subject subject : realmResults) {
                if (query.length() == 1) {
                    if (subject.getName().toUpperCase().startsWith(query.toUpperCase()))
                        results.add(subject);
                } else {
                    if (subject.getName().toUpperCase().contains(query.toUpperCase()))
                        results.add(subject);
                }
            }
        }
        return results;
    }

    public Subject getByName(String name) {
        return realm.where(Subject.class).equalTo("name", name).findFirst();
    }

    public List<Subject> getSubList() {
        RealmResults realmResults = realm.where(Subject.class).findAllSorted("name");
        return /*(realmResults.size() >= 5) ? realmResults.subList(0, 5) : */realmResults;
    }

}
