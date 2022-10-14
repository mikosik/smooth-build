package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;

public record TaskInfo(TaskKind kind, TagLoc tagLoc) {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind call, String tag, Loc loc) {
    this(call, new TagLoc(tag, loc));
  }

  public String tag() {
    return tagLoc.tag();
  }

  public Loc loc() {
    return tagLoc.loc();
  }
}
