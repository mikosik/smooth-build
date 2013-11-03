package org.smoothbuild.testing.plugin;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.smoothbuild.db.hash.Hash;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.File;

import com.google.common.hash.HashCode;

public class FakeFile implements File {
  private final Path path;
  private final HashCode hash;
  private final byte[] content;

  public FakeFile(Path path) {
    this(path, path.value());
  }

  public FakeFile(Path path, String content) {
    this(path, content.getBytes(CHARSET));
  }

  public FakeFile(Path path, byte[] bytes) {
    this.path = path;
    this.hash = Hash.function().hashBytes(bytes);
    this.content = bytes.clone();
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
    return new ByteArrayInputStream(content);
  }
}
