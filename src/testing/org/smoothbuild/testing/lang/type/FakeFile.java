package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.STypes.FILE;

import java.io.InputStream;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.value.CachedValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;

import com.google.common.hash.HashCode;

public class FakeFile extends CachedValue implements SFile {
  private final Path path;
  private final SBlob content;

  public FakeFile(Path path) {
    this(path, path.value());
  }

  public FakeFile(Path path, String content) {
    this(path, content.getBytes(CHARSET));
  }

  public FakeFile(Path path, byte[] bytes) {
    this(path, new FakeBlob(bytes));

  }

  public FakeFile(Path path, FakeBlob content) {
    super(FILE, calculateHash(path, content));
    this.path = path;
    this.content = content;
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public SBlob content() {
    return content;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }

  private static HashCode calculateHash(Path path, SBlob blob) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(blob.hash());
    marshaller.write(path);
    return Hash.bytes(marshaller.getBytes());
  }
}
