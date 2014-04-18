package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public interface ObjectMarshaller<T extends SValue> {
  public T read(HashCode hash);
}
