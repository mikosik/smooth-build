package org.smoothbuild.task.save;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.SFile;

public class FileSaver implements Saver<SFile> {
  private final BlobSaver blobSaver;

  public FileSaver(FileSystem smoothFileSystem) {
    this.blobSaver = new BlobSaver(smoothFileSystem);
  }

  public void save(Name name, SFile file) {
    blobSaver.save(name, file.content());
  }
}
