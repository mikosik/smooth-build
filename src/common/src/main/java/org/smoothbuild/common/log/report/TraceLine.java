package org.smoothbuild.common.log.report;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;

public record TraceLine(String called, Location location, TraceLine next) {
  public TraceLine {
    Objects.requireNonNull(called);
    Objects.requireNonNull(location);
  }

  @Override
  public String toString() {
    return "@ " + location.toString() + " " + Objects.toString(called, "");
  }
}
