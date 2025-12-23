package com.example.ticketero.model.enums;

public enum QueueType {
    CAJA("Caja", 5, 1, "C"),
    PERSONAL_BANKER("Personal Banker", 15, 2, "P"),
    EMPRESAS("Empresas", 20, 3, "E"),
    GERENCIA("Gerencia", 30, 4, "G");

    private final String displayName;
    private final int avgTimeMinutes;
    private final int priority;
    private final String prefix;

    QueueType(String displayName, int avgTimeMinutes, int priority, String prefix) {
        this.displayName = displayName;
        this.avgTimeMinutes = avgTimeMinutes;
        this.priority = priority;
        this.prefix = prefix;
    }

    public String getDisplayName() { return displayName; }
    public int getAvgTimeMinutes() { return avgTimeMinutes; }
    public int getPriority() { return priority; }
    public String getPrefix() { return prefix; }
    
    public static QueueType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("QueueType value cannot be null");
        }
        try {
            return QueueType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid QueueType: " + value + ". Valid values: CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA");
        }
    }
}