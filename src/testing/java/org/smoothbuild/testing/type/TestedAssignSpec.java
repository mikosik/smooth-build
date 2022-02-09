package org.smoothbuild.testing.type;

public interface TestedAssignSpec<T extends TestedT<?>> {
  public TestedAssign<T> assignment();

  public boolean allowed();

  public default T source() {
    return assignment().source();
  }

  public default T target() {
    return assignment().target();
  }

  public default String toStringImpl() {
    return assignment().toString() + " :" + (allowed() ? "allowed" : "illegal");
  }
}
