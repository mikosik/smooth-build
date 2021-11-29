package org.smoothbuild.exec.job;

import java.util.List;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;

import com.google.common.collect.ImmutableList;

public abstract class AbstractJob extends NalImpl implements Job {
  private final TypeH type;
  private final ImmutableList<Job> deps;

  public AbstractJob(TypeH type, List<Job> deps, Nal nal) {
    super(nal);
    this.type = type;
    this.deps = ImmutableList.copyOf(deps);
  }

  @Override
  public TypeH type() {
    return type;
  }

  @Override
  public ImmutableList<Job> deps() {
    return deps;
  }
}
