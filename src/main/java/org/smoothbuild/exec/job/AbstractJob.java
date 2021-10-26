package org.smoothbuild.exec.job;

import java.util.List;

import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public abstract class AbstractJob extends NalImpl implements Job {
  private final Type type;
  private final ImmutableList<Job> dependencies;

  public AbstractJob(Type type, List<Job> dependencies, Nal nal) {
    super(nal);
    this.type = type;
    this.dependencies = ImmutableList.copyOf(dependencies);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public ImmutableList<Job> dependencies() {
    return dependencies;
  }
}
