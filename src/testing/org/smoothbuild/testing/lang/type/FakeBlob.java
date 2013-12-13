package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.lang.type.STypes.BLOB;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.smoothbuild.command.SmoothContants;
import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.instance.CachedValue;
import org.smoothbuild.lang.type.SBlob;

public class FakeBlob extends CachedValue implements SBlob {
  private final byte[] data;

  public FakeBlob() {
    this(new byte[] { 1, 2, 3 });
  }

  public FakeBlob(String string) {
    this(string.getBytes(SmoothContants.CHARSET));
  }

  public FakeBlob(byte[] data) {
    super(BLOB, Hash.function().hashBytes(data));
    this.data = data.clone();
  }

  @Override
  public InputStream openInputStream() {
    return new ByteArrayInputStream(data);
  }
}
