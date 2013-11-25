package org.smoothbuild.io.cache.value;

import org.smoothbuild.lang.type.SValue;

import com.google.common.hash.HashCode;

public interface ReadValue<T extends SValue> {
  public T read(HashCode hash);
}
