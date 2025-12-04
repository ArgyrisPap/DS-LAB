package gr.hua.dit.steetfood.core.service.model;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Positive;

public record StartOrderRequest (@NotNull @Positive Long id) {


}
