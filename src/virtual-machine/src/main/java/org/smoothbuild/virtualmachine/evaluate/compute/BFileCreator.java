package org.smoothbuild.virtualmachine.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;

public class BFileCreator {
  private final FileSystem<FullPath> filesystem;
  private final FullPath projectPath;
  private final FileContentReader fileContentReader;
  private final BytecodeFactory factory;

  public BFileCreator(
      FileSystem<FullPath> filesystem,
      FullPath projectPath,
      FileContentReader fileContentReader,
      BytecodeFactory factory) {
    this.filesystem = filesystem;
    this.projectPath = projectPath;
    this.fileContentReader = fileContentReader;
    this.factory = factory;
  }

  public BArray createFiles(Path dir) throws IOException, BytecodeException {
    var fileArrayBuilder = factory.arrayBuilderWithElements(factory.fileType());
    for (PathIterator it = filesystem.filesRecursively(projectPath.append(dir)); it.hasNext(); ) {
      Path path = it.next();
      fileArrayBuilder.add(createFile(path, dir.append(path)));
    }
    return fileArrayBuilder.build();
  }

  public BTuple createFile(Path path, Path projectPath) throws IOException, BytecodeException {
    return factory.file(createContent(projectPath), createPath(path));
  }

  private BString createPath(Path path) throws BytecodeException {
    return factory.string(path.toString());
  }

  private BBlob createContent(Path path) throws IOException, BytecodeException {
    return fileContentReader.read(projectPath.append(path));
  }
}
