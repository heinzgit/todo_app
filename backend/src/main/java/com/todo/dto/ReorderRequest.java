package com.todo.dto;

import java.util.List;

public class ReorderRequest {
    private List<Long> taskIds;

    public ReorderRequest() {}

    public ReorderRequest(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getTaskIds() { return taskIds; }
    public void setTaskIds(List<Long> taskIds) { this.taskIds = taskIds; }
}
