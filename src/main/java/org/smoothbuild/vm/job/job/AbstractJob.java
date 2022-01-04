package org.smoothbuild.vm.job.job;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Loc;

public abstract class AbstractJob implements Job {
  private final TypeB type;
  private final Loc loc;

  public AbstractJob(TypeB type, Loc loc) {
    this.type = type;
    this.loc = loc;
  }

  @Override
  public TypeB type() {
    return type;
  }

  @Override
  public Loc loc() {
    return loc;
  }
}
