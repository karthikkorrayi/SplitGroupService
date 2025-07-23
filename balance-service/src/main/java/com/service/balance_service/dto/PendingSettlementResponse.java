package com.service.balance_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PendingSettlementResponse {
    private String balanceId;
    private Long fromUserId;
    private String fromUserName;
    private Long toUserId;
    private String toUserName;
    private BigDecimal amountOwed;
    private BigDecimal suggestedAmount;
    private long daysSinceLastTransaction;
    private Priority priority;
    private String description;
    private LocalDateTime lastUpdated;

    // Priority enum
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    // Default constructor
    public PendingSettlementResponse() {
    }

    // Getters and Setters
    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public BigDecimal getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(BigDecimal amountOwed) {
        this.amountOwed = amountOwed;
    }

    public BigDecimal getSuggestedAmount() {
        return suggestedAmount;
    }

    public void setSuggestedAmount(BigDecimal suggestedAmount) {
        this.suggestedAmount = suggestedAmount;
    }

    public long getDaysSinceLastTransaction() {
        return daysSinceLastTransaction;
    }

    public void setDaysSinceLastTransaction(long daysSinceLastTransaction) {
        this.daysSinceLastTransaction = daysSinceLastTransaction;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper methods
    public String getPriorityDisplayName() {
        switch (priority) {
            case HIGH: return "High Priority";
            case MEDIUM: return "Medium Priority";
            case LOW: return "Low Priority";
            default: return "Unknown";
        }
    }

    public String getFormattedDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        String priorityText = "";
        switch (priority) {
            case HIGH:
                priorityText = " (High Priority)";
                break;
            case MEDIUM:
                priorityText = " (Medium Priority)";
                break;
            case LOW:
                priorityText = "";
                break;
        }
        return fromUserName + " owes " + toUserName + " $" + amountOwed + priorityText;
    }
}