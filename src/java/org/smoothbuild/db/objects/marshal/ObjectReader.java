package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public interface ObjectReader<T extends SValue> {
  public T read(HashCode hash);
}
