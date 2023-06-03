package de.groodian.network;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class DataPackage extends ArrayList<Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1884397299414251338L;

    public DataPackage(Object... data) {
        Collections.addAll(this, data);
    }

}
