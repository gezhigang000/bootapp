package cc.starapp.bootapp.core;

public class EmbeddedAppConfig {

    private String[] libAbsoluteDirs;

    private String includeByNameRegex;

    private String excludeByNameRegex;

    public String[] getLibAbsoluteDirs() {
        return libAbsoluteDirs;
    }

    public void setLibAbsoluteDirs(String[] libAbsoluteDirs) {
        this.libAbsoluteDirs = libAbsoluteDirs;
    }

    public String getIncludeByNameRegex() {
        return includeByNameRegex;
    }

    public void setIncludeByNameRegex(String includeByNameRegex) {
        this.includeByNameRegex = includeByNameRegex;
    }

    public String getExcludeByNameRegex() {
        return excludeByNameRegex;
    }

    public void setExcludeByNameRegex(String excludeByNameRegex) {
        this.excludeByNameRegex = excludeByNameRegex;
    }
}
