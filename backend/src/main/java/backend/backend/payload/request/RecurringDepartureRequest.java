package backend.backend.payload.request;

import backend.backend.dto.DepartureDto;

public class RecurringDepartureRequest {
    private DepartureDto templateDto;
    private int intervalDays;
    private int count;

    // Getters and Setters
    public DepartureDto getTemplateDto() {
        return templateDto;
    }

    public void setTemplateDto(DepartureDto templateDto) {
        this.templateDto = templateDto;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}