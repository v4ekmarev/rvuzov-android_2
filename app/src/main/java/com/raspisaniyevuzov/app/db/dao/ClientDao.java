package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.Client;
import com.raspisaniyevuzov.app.util.DbUtil;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class ClientDao extends BaseDAO {

    public ClientDao() {
        super();
    }

    public static ClientDao getInstance() {
        return new ClientDao();
    }

    public Client getClient() {
        Client client = realm.where(Client.class).findFirst();
        if (client == null) {
            realm.beginTransaction();
            Client newClient = new Client();
            newClient.setId(DbUtil.getNewUid());
            realm.copyToRealm(newClient);
            realm.commitTransaction();
        }
        return client;
    }

    public void saveToken(String token) {
        realm.beginTransaction();
        Client client = getClient();
        client.setToken(token);
        realm.commitTransaction();
    }

}
