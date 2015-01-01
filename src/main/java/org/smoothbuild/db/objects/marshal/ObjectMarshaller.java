package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public interface ObjectMarshaller<T extends Value> {
  public T read(HashCode hash);
}
