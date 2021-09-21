package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.Named;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public abstract class AbstractJob extends Nal implements Job {
  private final Type type;
  private final ImmutableList<Job> dependencies;

  public AbstractJob(Type type, List<Job> dependencies, Named named) {
    super(named);
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
