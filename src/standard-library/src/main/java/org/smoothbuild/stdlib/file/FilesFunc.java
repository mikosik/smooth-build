package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.bucket.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.PathIterator;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FilesFunc {
  public static BValue func(Container container, BTuple args) throws BytecodeException {
    BString dir = (BString) args.get(0);
    Path path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
    try {
      return readFiles(container, path);
    } catch (IOException e) {
      container.log().error(e.getMessage());
      return null;
    }
  }

  private static BArray readFiles(Container container, Path dir)
      throws IOException, BytecodeException {
    var fileArrayBuilder =
        container.factory().arrayBuilderWithElements(container.factory().fileType());
    var reader = new FileReader(container);
    for (PathIterator it = recursivePathsIterator(container.bucket(), dir); it.hasNext(); ) {
      Path path = it.next();
      fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
    }
    return fileArrayBuilder.build();
  }
}
