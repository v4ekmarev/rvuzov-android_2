package com.raspisaniyevuzov.app.db.manager;

import com.raspisaniyevuzov.app.db.dao.UserProfileDao;
import com.raspisaniyevuzov.app.db.model.UserProfile;
import com.raspisaniyevuzov.app.event.UserProfileChangedEvent;

import de.greenrobot.event.EventBus;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 08.12.2015.
 */
public class UserProfileManager {

    public static UserProfile getCurrentUserProfile() {
        RealmResults<UserProfile> userProfiles = UserProfileDao.getInstance().getAll(UserProfile.class);
        if (userProfiles != null && !userProfiles.isEmpty()) return userProfiles.get(0);
        else return null;
    }

    public static void updateUserProfile(String name, String avatar, String email, String groupId) {
        UserProfileDao.getInstance().updateUserProfile(name, avatar, email, groupId);
    }

}
