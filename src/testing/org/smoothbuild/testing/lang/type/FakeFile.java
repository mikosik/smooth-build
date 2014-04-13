package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.lang.base.STypes.FILE;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.instance.CachedValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class FakeFile extends CachedValue implements SFile {
  private final Path path;
  private final SBlob content;

  public FakeFile(Path path) {
    this(path, path.value());
  }

  public FakeFile(Path path, String content) {
    this(path, new FakeBlob(content));
  }

  public FakeFile(Path path, byte[] bytes) {
    this(path, new FakeBlob(bytes));
  }

  public FakeFile(Path path, SBlob content) {
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

  private static HashCode calculateHash(Path path, SBlob blob) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(blob.hash());
    marshaller.write(path);
    return Hash.bytes(marshaller.getBytes());
  }
}
