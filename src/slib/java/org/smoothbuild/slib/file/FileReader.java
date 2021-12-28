package org.smoothbuild.slib.file;

import java.io.IOException;

import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSource;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public TupleB createFile(Path path, Path projectPath) throws IOException {
    return container.factory().file(createPath(path), createContent(projectPath));
  }

  private StringB createPath(Path path) {
    return container.factory().string(path.toString());
  }

  private BlobB createContent(Path path) throws IOException {
    try (BufferedSource source = container.fileSystem().source(path)) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
