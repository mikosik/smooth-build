package org.smoothbuild.slib.file;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.vm.compute.Container;

import okio.BufferedSource;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public TupleB createFile(PathS path, PathS projectPath) throws IOException {
    return container.factory().file(createContent(projectPath), createPath(path));
  }

  private StringB createPath(PathS path) {
    return container.factory().string(path.toString());
  }

  private BlobB createContent(PathS path) throws IOException {
    try (BufferedSource source = container.fileSystem().source(path)) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
