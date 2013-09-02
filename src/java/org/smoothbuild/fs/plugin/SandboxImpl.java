package org.smoothbuild.fs.plugin;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.Sandbox;

public class SandboxImpl implements Sandbox {
  private final FileSystem fileSystem;
  private final Path root;

  private FileList resultFileList;
  private File resultFile;

  public SandboxImpl(FileSystem fileSystem, Path root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  @Override
  public FileList resultFileList() {
    checkState(resultFile == null,
        "Cannot call resultFileList() when resultFile() has been called.");

    if (resultFileList == null) {
      resultFileList = new FileListImpl(fileSystem, root);
    }

    return resultFileList;
  }

  @Override
  public File resultFile(Path path) {
    checkState(resultFile == null, "Cannot call resultFile() twice.");
    checkState(resultFileList == null,
        "Cannot call resultFile() when resultFileList() has been called.");

    resultFile = new FileImpl(fileSystem, root, path);

    return resultFile;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }
}
