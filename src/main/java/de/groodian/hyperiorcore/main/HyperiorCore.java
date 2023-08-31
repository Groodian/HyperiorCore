package de.groodian.hyperiorcore.main;

public class HyperiorCore {

    public static final String DB_ADDRESS = "localhost";
    public static final int DB_PORT = 5444;
    public static final String DB_DATABASE = "postgres";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "toor";

    public static Main getPaper() {
        return Main.getInstance();
    }

    public static VelocityMain getVelocity() {
        return VelocityMain.getInstance();
    }

}
