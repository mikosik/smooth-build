package org.smoothbuild.io.cache.value;

import org.smoothbuild.lang.type.Value;

import com.google.common.hash.HashCode;

public interface ReadValue<T extends Value> {
  public T read(HashCode hash);
}
