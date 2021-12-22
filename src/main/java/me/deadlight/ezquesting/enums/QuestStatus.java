package me.deadlight.ezquesting.enums;

public enum QuestStatus {
    ACTIVE(1),
    COMPLETED(2),
    NOTSTARTED(0);


    public final int statusNumber;
    QuestStatus(int statusNumber) {
        this.statusNumber = statusNumber;
    }

    public static QuestStatus statusNumber(int value) {
        if (value == 1) {
            return QuestStatus.ACTIVE;
        } else if (value == 2) {
            return QuestStatus.COMPLETED;
        } else if (value == 0) {
            return QuestStatus.NOTSTARTED;
        } else {
            return null;
        }
    }

}
