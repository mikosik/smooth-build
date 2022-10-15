package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.base.Trace;

public record TaskInfo(TaskKind kind, TagLoc tagLoc, Trace trace) {
  public String tag() {
    return tagLoc.tag();
  }

  public Loc loc() {
    return tagLoc.loc();
  }
}
