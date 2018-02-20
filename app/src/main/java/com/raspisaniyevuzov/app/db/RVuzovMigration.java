package com.raspisaniyevuzov.app.db;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by SAPOZHKOV on 11.12.2015.
 */
public class RVuzovMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        // Migrate to version 2: Add a new class - UserProfile
        if (oldVersion == 0) {
            schema.create("UserProfile")
                    .addField("id", String.class, FieldAttribute.INDEXED)
                    .addField("name", String.class)
                    .addField("email", String.class)
                    .addField("avatar", String.class)
                    .addField("group", String.class);
            oldVersion++;
        }
        if (oldVersion == 1)
            schema.get("UserProfile").setNullable("id", false).addPrimaryKey("id");
    }

}
