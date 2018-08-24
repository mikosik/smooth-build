package org.smoothbuild.builtin.file;

import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.task.exec.Container;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public Struct createFile(Path path, Path projectPath) throws IOException {
    return container.create().file(createPath(path), createContent(projectPath));
  }

  private SString createPath(Path path) {
    return container.create().string(path.value());
  }

  private Blob createContent(Path path) throws IOException {
    BlobBuilder builder = container.create().blobBuilder();
    builder.sink().writeAll(container.fileSystem().source(path));
    return builder.build();
  }
}
