package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;

public class Argument {
  public static <V> Promise<Maybe<V>> argument(V value) {
    return promise(some(value));
  }
}
