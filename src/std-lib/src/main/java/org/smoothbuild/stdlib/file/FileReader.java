package org.smoothbuild.stdlib.file;

import java.io.IOException;

import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.evaluate.compute.Container;

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
