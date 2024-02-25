package org.smoothbuild.common.function;

@FunctionalInterface
public interface Consumer0<T extends Throwable> {
  public void accept() throws T;
}
