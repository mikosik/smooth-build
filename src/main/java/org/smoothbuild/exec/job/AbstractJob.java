package org.smoothbuild.exec.job;

import java.util.List;

import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;

import com.google.common.collect.ImmutableList;

public abstract class AbstractJob extends NalImpl implements Job {
  private final TypeHV type;
  private final ImmutableList<Job> dependencies;

  public AbstractJob(TypeHV type, List<Job> dependencies, Nal nal) {
    super(nal);
    this.type = type;
    this.dependencies = ImmutableList.copyOf(dependencies);
  }

  @Override
  public TypeHV type() {
    return type;
  }

  @Override
  public ImmutableList<Job> dependencies() {
    return dependencies;
  }
}
