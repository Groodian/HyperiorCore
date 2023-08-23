package de.groodian.hyperiorcore.command;

public class HArgument {

    private final String name;
    private final boolean multipleWords;
    private final HTabCompleteType hTabCompleteType;

    public HArgument(String name, boolean multipleWords, HTabCompleteType hTabCompleteType) {
        this.name = name;
        this.multipleWords = multipleWords;
        this.hTabCompleteType = hTabCompleteType;
    }

    public HArgument(String name, HTabCompleteType hTabCompleteType) {
        this(name, false, hTabCompleteType);
    }

    public HArgument(String name) {
        this(name, false, HTabCompleteType.NONE);
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

}
