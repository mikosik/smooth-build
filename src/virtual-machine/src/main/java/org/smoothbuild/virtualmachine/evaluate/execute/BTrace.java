package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.log.report.TraceLine;

public final class BTrace extends Trace {
  public static BTrace bTrace(Hash call, Hash called, BTrace next) {
    return new BTrace(new Line(call, called, next.topLine()));
  }

  public BTrace() {
    this(null);
  }

  public BTrace(Line topLine) {
    super(topLine);
  }

  @Override
  public Line topLine() {
    return (Line) super.topLine();
  }

  public static record Line(Hash call, Hash called, Line next) implements TraceLine {
    @Override
    public String toString() {
      return call.toString() + " " + called.toString();
    }
  }
}
