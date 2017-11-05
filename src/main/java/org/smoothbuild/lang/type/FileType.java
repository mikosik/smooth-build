package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.SFile;

public class FileType extends Type {
  protected FileType() {
    super("File", SFile.class);
  }
}
