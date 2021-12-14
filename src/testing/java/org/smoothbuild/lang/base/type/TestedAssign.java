package org.smoothbuild.lang.base.type;

public interface TestedAssign<T extends TestedT<?>> {
  public T target();
  public T source();
}
