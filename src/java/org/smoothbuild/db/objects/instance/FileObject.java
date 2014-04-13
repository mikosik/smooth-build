package org.smoothbuild.db.objects.instance;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.FILE;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class FileObject extends AbstractObject implements SFile {
  private final Path path;
  private final SBlob content;

  public FileObject(Path path, SBlob content, HashCode hash) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public SBlob content() {
    return content;
  }
}
