package org.smoothbuild.common.log.report;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.location.Location;

public record TraceLine(
    String called, Location location, int depth, @Nullable TraceLine next) {
  public TraceLine(String called, Location location, @Nullable TraceLine next) {
    this(called, location, next == null ? 1 : 1 + next.depth(), next);
  }

  @Override
  public String toString() {
    return "@ " + location + " " + called;
  }
}
