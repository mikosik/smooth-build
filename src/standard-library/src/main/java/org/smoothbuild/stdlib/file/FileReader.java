package org.smoothbuild.stdlib.file;

import static okio.Okio.buffer;

import java.io.IOException;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public BTuple createFile(Path path, Path projectPath) throws IOException, BytecodeException {
    return container.factory().file(createContent(projectPath), createPath(path));
  }

  private BString createPath(Path path) throws BytecodeException {
    return container.factory().string(path.toString());
  }

  private BBlob createContent(Path path) throws IOException, BytecodeException {
    try (var source = buffer(container.bucket().source(path))) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
