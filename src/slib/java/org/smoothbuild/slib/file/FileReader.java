package org.smoothbuild.slib.file;

import java.io.IOException;

import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;

import okio.BufferedSource;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public Tuple createFile(Path path, Path projectPath) throws IOException {
    return container.factory().file(createPath(path), createContent(projectPath));
  }

  private SString createPath(Path path) {
    return container.factory().string(path.toString());
  }

  private Blob createContent(Path path) throws IOException {
    try (BufferedSource source = container.fileSystem().source(path)) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
