package collection.compare.test;

public enum Suit {
    SPADE("\u2660"), //\u2660
    HEART("\u2665"), //\u2665
    DIAMOND("\u2666"), //\u2666
    CLUB("\u2663"); //\u2663

    private String icon;

    Suit(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
