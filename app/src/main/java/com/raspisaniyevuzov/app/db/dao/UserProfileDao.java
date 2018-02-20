package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.UserProfile;
import com.raspisaniyevuzov.app.util.DbUtil;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class UserProfileDao extends BaseDAO {

    public UserProfileDao() {
        super();
    }

    public static UserProfileDao getInstance() {
        return new UserProfileDao();
    }

    private UserProfile getUserProfile() {
        UserProfile profile = realm.where(UserProfile.class).findFirst();
        if (profile == null) {
            realm.beginTransaction();
            profile = new UserProfile();
            profile.setId(DbUtil.getNewUid());
            realm.copyToRealm(profile);
            realm.commitTransaction();
        }
        return profile;
    }

    public void updateUserProfile(String name, String avatar, String email, String groupId) {
        UserProfile profile = getUserProfile();
        realm.beginTransaction();
        profile.setAvatar(avatar);
        profile.setName(name);
        profile.setEmail(email);
        profile.setGroup(groupId);
        realm.copyToRealmOrUpdate(profile);
        realm.commitTransaction();
    }

}
