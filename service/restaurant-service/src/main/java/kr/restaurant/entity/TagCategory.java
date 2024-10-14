package kr.restaurant.entity;

public enum TagCategory {
    방문목적("방문목적"),
    분위기("분위기"),
    편의시설("편의시설");

    private final String displayName;

    TagCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}


