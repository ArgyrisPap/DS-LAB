package gr.hua.dit.steetfood.core.port;

import gr.hua.dit.steetfood.core.port.impl.dto.RouteInfo;

public interface RoutePort {

    RouteInfo getRoute (Double startLat, Double startLon, Double endLat, Double endLon);
}
