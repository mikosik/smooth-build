package org.smoothbuild.testing.type;

public interface TestedAssignSpec<T extends TestedT<?>> {
  public TestedAssign<T> assignment();

  public boolean allowed();

  public T source();

  public T target();
}
