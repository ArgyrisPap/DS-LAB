package gr.hua.dit.steetfood.core.port.impl.dto;

public record RouteInfo(
    double durationSeconds,
    double distance
) {
    public long durationMinutes() {
        return Math.round(durationSeconds / 60);
    }
}
