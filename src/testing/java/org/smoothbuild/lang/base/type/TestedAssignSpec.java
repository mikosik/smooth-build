package org.smoothbuild.lang.base.type;

public interface TestedAssignSpec<T extends TestedT<?>> {
  public TestedAssign<T> assignment();

  public boolean allowed();

  public default T source() {
    return assignment().source();
  }

  public default T target() {
    return assignment().target();
  }
}
