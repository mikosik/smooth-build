package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.Types.FILE;

import com.google.common.hash.HashCode;

/**
 * File value in smooth language.
 */
public class SFile extends Value {
  private final SString path;
  private final Blob content;

  public SFile(HashCode hash, SString path, Blob content) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
  }

  public SString path() {
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
