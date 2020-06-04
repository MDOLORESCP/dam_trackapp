package dam.trackapp.modelos;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Modelo {
    private String _id;

    protected Modelo() {

    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }
}
