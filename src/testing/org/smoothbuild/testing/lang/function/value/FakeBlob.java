package org.smoothbuild.testing.lang.function.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.AbstractValue;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.Blob;

public class FakeBlob extends AbstractValue implements Blob {
  private final byte[] data;

  public FakeBlob(byte[] data) {
    super(Type.BLOB, Hash.function().hashBytes(data));
    this.data = data.clone();
  }

  @Override
  public InputStream openInputStream() {
    return new ByteArrayInputStream(data);
  }
}
