package org.smoothbuild.task.base;


public abstract class InternalTask implements Task {
  @Override
  public boolean isInternal() {
    return true;
  }
}
