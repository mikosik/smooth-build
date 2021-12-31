package org.smoothbuild.vm.job.job;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;

public abstract class AbstractJob extends NalImpl implements Job {
  private final TypeB type;

  public AbstractJob(TypeB type, Nal nal) {
    super(nal);
    this.type = type;
  }

  @Override
  public TypeB type() {
    return type;
  }
}
