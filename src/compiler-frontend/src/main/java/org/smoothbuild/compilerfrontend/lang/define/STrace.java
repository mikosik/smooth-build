package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.log.report.TraceLine;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

/**
 * Smooth stack trace.
 */
public final class STrace extends Trace {
  public STrace() {
    this(null);
  }

  public STrace(Line topLine) {
    super(topLine);
  }

  public record Line(String called, Location location, Line next) implements TraceLine {
    public Line {
      Objects.requireNonNull(called);
      Objects.requireNonNull(location);
    }

    @Override
    public String toString() {
      return "@ " + location.toString() + " " + Objects.toString(called, "");
    }
  }
}
