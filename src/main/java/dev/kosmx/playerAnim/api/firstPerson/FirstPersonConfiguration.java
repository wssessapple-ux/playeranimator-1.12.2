package dev.kosmx.playerAnim.api.firstPerson;

public class FirstPersonConfiguration {
    boolean showRightArm = false;
    boolean showLeftArm = false;
    boolean showRightItem = true;
    boolean showLeftItem = true;

    public FirstPersonConfiguration() {
    }

    public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem) {
        this.showRightArm = showRightArm;
        this.showLeftArm = showLeftArm;
        this.showRightItem = showRightItem;
        this.showLeftItem = showLeftItem;
    }

    public boolean isShowRightArm() {
        return showRightArm;
    }

    public FirstPersonConfiguration setShowRightArm(boolean showRightArm) {
        this.showRightArm = showRightArm;
        return this;
    }

    public boolean isShowLeftArm() {
        return showLeftArm;
    }

    public FirstPersonConfiguration setShowLeftArm(boolean showLeftArm) {
        this.showLeftArm = showLeftArm;
        return this;
    }

    public boolean isShowRightItem() {
        return showRightItem;
    }

    public FirstPersonConfiguration setShowRightItem(boolean showRightItem) {
        this.showRightItem = showRightItem;
        return this;
    }

    public boolean isShowLeftItem() {
        return showLeftItem;
    }

    public FirstPersonConfiguration setShowLeftItem(boolean showLeftItem) {
        this.showLeftItem = showLeftItem;
        return this;
    }
}
