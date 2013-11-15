package org.smoothbuild.testing.lang.function.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.InputStream;

import org.smoothbuild.io.db.hash.Hash;
import org.smoothbuild.io.db.hash.Marshaller;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.File;

import com.google.common.hash.HashCode;

public class FakeFile implements File {
  private final Path path;
  private final Blob content;
  private final HashCode hash;

  public FakeFile(Path path) {
    this(path, path.value());
  }

  public FakeFile(Path path, String content) {
    this(path, content.getBytes(CHARSET));
  }

  public FakeFile(Path path, byte[] bytes) {
    this.path = path;
    this.content = new FakeBlob(bytes);
    this.hash = calculateHash(path, content);
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
  public Blob content() {
    return content;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }

  private static HashCode calculateHash(Path path, Blob blob) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(blob.hash());
    marshaller.write(path);
    return Hash.bytes(marshaller.getBytes());
  }
}
