package org.smoothbuild.virtualmachine.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.PathIterator;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

public class FileReader {
  private final Container container;
  private final Filesystem filesystem;
  private final FullPath projectPath;

  public FileReader(Container container, Filesystem filesystem, FullPath projectPath) {
    this.container = container;
    this.filesystem = filesystem;
    this.projectPath = projectPath;
  }

  public BArray createFiles(Path dir) throws IOException, BytecodeException {
    var fileArrayBuilder =
        container.factory().arrayBuilderWithElements(container.factory().fileType());
    for (PathIterator it = filesystem.filesRecursively(projectPath.append(dir)); it.hasNext(); ) {
      Path path = it.next();
      fileArrayBuilder.add(createFile(path, dir.append(path)));
    }
    return fileArrayBuilder.build();
  }

  public BTuple createFile(Path path, Path projectPath) throws IOException, BytecodeException {
    return container.factory().file(createContent(projectPath), createPath(path));
  }

  private BString createPath(Path path) throws BytecodeException {
    return container.factory().string(path.toString());
  }

  private BBlob createContent(Path path) throws IOException, BytecodeException {
    return container.fileContentReader().read(projectPath.append(path));
  }
}
