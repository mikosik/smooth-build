package org.smoothbuild.testing.plugin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.io.db.hash.Hash;
import org.smoothbuild.plugin.Blob;

import com.google.common.hash.HashCode;

public class FakeBlob implements Blob {
  private final HashCode hash;
  private final byte[] data;

  public FakeBlob(byte[] data) {
    this.hash = Hash.function().hashBytes(data);
    this.data = data.clone();
  }

  @Override
  public Type type() {
    return Type.BLOB;
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public InputStream openInputStream() {
    return new ByteArrayInputStream(data);
  }
}
