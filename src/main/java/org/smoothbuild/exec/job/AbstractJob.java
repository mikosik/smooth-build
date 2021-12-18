package org.smoothbuild.exec.job;

import java.util.List;

import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;

import com.google.common.collect.ImmutableList;

public abstract class AbstractJob extends NalImpl implements Job {
  private final TypeB type;
  private final ImmutableList<Job> deps;

  public AbstractJob(TypeB type, List<Job> deps, Nal nal) {
    super(nal);
    this.type = type;
    this.deps = ImmutableList.copyOf(deps);
  }

  @Override
  public TypeB type() {
    return type;
  }

  @Override
  public ImmutableList<Job> deps() {
    return deps;
  }
}
