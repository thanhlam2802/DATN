package backend.backend.dto;

import backend.backend.entity.TourStatus;

public class UpdateTourStatusDTO {
    private TourStatus status;

    public TourStatus getStatus() {
        return status;
    }

    public void setStatus(TourStatus status) {
        this.status = status;
    }
}
