package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;

public record TaskInfo(TaskKind kind, String tag, Loc loc) {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind kind, TagLoc tagLoc) {
    this(kind, tagLoc.tag(), tagLoc.loc());
  }

  public TagLoc tagLoc() {
    return new TagLoc(tag, loc);
  }
}
