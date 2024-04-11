package org.smoothbuild.common.log.report;

public interface TraceLine<T extends TraceLine<T>> {
  public T next();
}
