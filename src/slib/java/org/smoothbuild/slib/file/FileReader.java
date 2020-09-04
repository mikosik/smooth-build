package org.smoothbuild.slib.file;

import java.io.IOException;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSource;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public Tuple createFile(Path path, Path projectPath) throws IOException {
    return container.factory().file(createPath(path), createContent(projectPath));
  }

  private Str createPath(Path path) {
    return container.factory().string(path.toString());
  }

  private Blob createContent(Path path) throws IOException {
    try (BufferedSource source = container.fileSystem().source(path)) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
