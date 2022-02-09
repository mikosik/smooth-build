package org.smoothbuild.testing.type;

public interface TestedAssign<T extends TestedT<?>> {
  public T target();
  public T source();
}
