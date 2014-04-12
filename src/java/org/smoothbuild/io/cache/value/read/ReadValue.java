package org.smoothbuild.io.cache.value.read;

import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public interface ReadValue<T extends SValue> {
  public T read(HashCode hash);
}
