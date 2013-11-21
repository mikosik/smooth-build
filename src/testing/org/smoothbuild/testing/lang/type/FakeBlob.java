package org.smoothbuild.testing.lang.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.AbstractValue;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.Type;

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
