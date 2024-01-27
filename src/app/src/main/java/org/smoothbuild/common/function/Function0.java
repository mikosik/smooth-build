package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function0<R, T extends Throwable> {
  public R apply() throws T;
}
