package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.execute.TaskKind;

public class Task {
  private final TypeB outputT;
  private final boolean isPure;
  private final TaskKind kind;
  private final TagLoc tagLoc;
  private final TraceS trace;

  public Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    this(outputT, kind, tagLoc, trace, true);
  }

  public Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace, boolean isPure) {
    this.outputT = outputT;
    this.kind = kind;
    this.tagLoc = tagLoc;
    this.trace = trace;
    this.isPure = isPure;
  }

  public TaskKind kind() {
    return kind;
  }

  public String tag() {
    return tagLoc.tag();
  }

  public Loc loc() {
    return tagLoc.loc();
  }

  public TypeB outputT() {
    return outputT;
  }

  public boolean isPure() {
    return isPure;
  }
}
