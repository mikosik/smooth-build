package org.smoothbuild.testing.plugin;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.InputStream;

import org.smoothbuild.db.hash.Hash;
import org.smoothbuild.db.hash.Marshaller;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.Blob;
import org.smoothbuild.plugin.File;

import com.google.common.hash.HashCode;

public class FakeFile implements File {
  private final Path path;
  private final Blob blob;
  private final HashCode hash;

  public FakeFile(Path path) {
    this(path, path.value());
  }

  public FakeFile(Path path, String content) {
    this(path, content.getBytes(CHARSET));
  }

  public FakeFile(Path path, byte[] bytes) {
    this.path = path;
    this.blob = new FakeBlob(bytes);
    this.hash = calculateHash(path, blob);
  }

  @Override
  public Type type() {
    return Type.FILE;
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public InputStream openInputStream() {
    return blob.openInputStream();
  }

  private static HashCode calculateHash(Path path, Blob blob) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(blob.hash());
    marshaller.write(path);
    return Hash.bytes(marshaller.getBytes());
  }
}
