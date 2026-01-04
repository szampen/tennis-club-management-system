package com.tennis.util;

import com.tennis.domain.SurfaceType;

public class CourtFilter {
    private SurfaceType surfaceType;
    private Boolean hasRoof;
    private Boolean availableForReservations;
    private CourtSort courtSort;
    private SortDirection direction = SortDirection.ASC;

    public void setAvailableForReservations(Boolean availableForReservations) {
        this.availableForReservations = availableForReservations;
    }

    public void setCourtSort(CourtSort courtSort) {
        this.courtSort = courtSort;
    }

    public void setHasRoof(Boolean hasRoof) {
        this.hasRoof = hasRoof;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public Boolean getAvailableForReservations() {
        return availableForReservations;
    }

    public Boolean getHasRoof() {
        return hasRoof;
    }

    public CourtSort getCourtSort() {
        return courtSort;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}
