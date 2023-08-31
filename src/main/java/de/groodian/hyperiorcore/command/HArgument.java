package de.groodian.hyperiorcore.command;

public class HArgument {

    private final String name;
    private final boolean multipleWords;
    private final HTabCompleteType hTabCompleteType;
    private final boolean optional;

    public HArgument(String name, boolean multipleWords, HTabCompleteType hTabCompleteType, boolean optional) {
        this.name = name;
        this.multipleWords = multipleWords;
        this.hTabCompleteType = hTabCompleteType;
        this.optional = optional;
    }

    public HArgument(String name, HTabCompleteType hTabCompleteType) {
        this(name, false, hTabCompleteType, false);
    }

    public HArgument(String name) {
        this(name, false, HTabCompleteType.NONE, false);
    }


    public String getName() {
        return name;
    }

    public boolean isMultipleWords() {
        return multipleWords;
    }

    public HTabCompleteType gethTabCompleteType() {
        return hTabCompleteType;
    }

    public boolean isOptional() {
        return optional;
    }

}
