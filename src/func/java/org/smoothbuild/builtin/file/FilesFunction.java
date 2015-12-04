package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;

public class FilesFunction {
  @SmoothFunction
  @NotCacheable
  public static Array<SFile> files(
      ContainerImpl container,
      @Name("dir") SString dir) {
    Path path = validatedProjectPath("dir", dir);
    FileSystem fileSystem = container.projectFileSystem();

    if (path.startsWith(SMOOTH_DIR)) {
      throw new ErrorMessage("Listing files from " + SMOOTH_DIR + " dir is not allowed.");
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(container, fileSystem, path);
      case FILE:
        throw new ErrorMessage("Dir " + path + " doesn't exist. It is a file.");
      case NOTHING:
        throw new ErrorMessage("Dir " + path + " doesn't exist.");
      default:
        throw new FileSystemException("Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array<SFile> readFiles(ContainerImpl container, FileSystem fileSystem, Path dir) {
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    FileReader reader = new FileReader(container);
    if (dir.isRoot()) {
      for (Path path : fileSystem.files(Path.root())) {
        if (!path.equals(SMOOTH_DIR)) {
          PathState pathState = fileSystem.pathState(path);
          switch (pathState) {
            case DIR:
              for (Path currentPath : recursiveFilesIterable(fileSystem, path)) {
                Path projectPath = path.append(currentPath);
                fileArrayBuilder.add(reader.createFile(projectPath, projectPath));
              }
              break;
            case FILE:
              fileArrayBuilder.add(reader.createFile(path, path));
              break;
            default:
              throw new RuntimeException("Unexpected case: " + pathState);
          }
        }
      }
    } else {
      for (Path path : recursiveFilesIterable(fileSystem, dir)) {
        fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
      }
    }
    return fileArrayBuilder.build();
  }
}
