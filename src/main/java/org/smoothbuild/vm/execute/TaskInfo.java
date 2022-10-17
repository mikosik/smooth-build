package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;

public record TaskInfo(TaskKind kind, TagLoc tagLoc, TraceS trace) {
  public String tag() {
    return tagLoc.tag();
  }

  public Loc loc() {
    return tagLoc.loc();
  }
}
