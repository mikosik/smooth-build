package org.smoothbuild.stdlib.file;

import java.io.IOException;
import okio.BufferedSource;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public TupleB createFile(PathS path, PathS projectPath) throws IOException, BytecodeException {
    return container.factory().file(createContent(projectPath), createPath(path));
  }

  private StringB createPath(PathS path) throws BytecodeException {
    return container.factory().string(path.toString());
  }

  private BlobB createContent(PathS path) throws IOException, BytecodeException {
    try (BufferedSource source = container.fileSystem().source(path)) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
