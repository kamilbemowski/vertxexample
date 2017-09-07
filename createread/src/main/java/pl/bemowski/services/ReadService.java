package pl.bemowski.services;

import pl.bemowski.Todo;

/**
 * Created by Kamil Bemowski on 2017-08-31.
 */
public class ReadService extends BaseService {

    public Todo read(long id) {
        return database.get(id);
    }
}
