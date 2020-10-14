package de.groodian.network;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPackage extends ArrayList<Object> implements Serializable {

    private static final long serialVersionUID = 1884397299414251338L;

    public DataPackage(Object... data) {
        for (Object current : data) {
            this.add(current);
        }
    }

}
