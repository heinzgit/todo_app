package com.todo.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RescheduleRequest {
    @NotNull(message = "New due date is required")
    private LocalDate newDueDate;

    public RescheduleRequest() {}

    public RescheduleRequest(LocalDate newDueDate) {
        this.newDueDate = newDueDate;
    }

    public LocalDate getNewDueDate() { return newDueDate; }
    public void setNewDueDate(LocalDate newDueDate) { this.newDueDate = newDueDate; }
}
