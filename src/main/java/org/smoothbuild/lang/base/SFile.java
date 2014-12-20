package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.Types.FILE;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;

/**
 * Smooth File. File value in smooth language.
 */
public class SFile extends AbstractValue {
  private final Path path;
  private final Blob content;

  public SFile(HashCode hash, Path path, Blob content) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
  }

  public Path path() {
    return path;
  }

  public Blob content() {
    return content;
  }

  @Override
  public String toString() {
    return "File(" + path + " " + content.toString() + ")";
  }
}
