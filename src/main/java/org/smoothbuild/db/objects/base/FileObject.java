package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.Types.FILE;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class FileObject extends AbstractObject implements SFile {
  private final Path path;
  private final Blob content;

  public FileObject(HashCode hash, Path path, Blob content) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
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
  public String toString() {
    return "File(" + path + " " + content.toString() + ")";
  }
}
