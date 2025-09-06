package org.smoothbuild.common.log.report;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.location.Location;

public record TraceLine(String called, Location location, @Nullable TraceLine next) {
  @Override
  public String toString() {
    return "@ " + location + " " + called;
  }
}
