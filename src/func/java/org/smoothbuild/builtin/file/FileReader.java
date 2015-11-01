package org.smoothbuild.builtin.file;

import static org.smoothbuild.util.Streams.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;

public class FileReader {
  private final ContainerImpl container;

  public FileReader(ContainerImpl container) {
    this.container = container;
  }

  public SFile createFile(Path path, Path projectPath) {
    return container.create().file(path, createContent(projectPath));
  }

  private Blob createContent(Path path) {
    InputStream inputStream = container.projectFileSystem().openInputStream(path);
    BlobBuilder contentBuilder = container.create().blobBuilder();
    doCopy(inputStream, contentBuilder.openOutputStream());
    return contentBuilder.build();
  }

  private static void doCopy(InputStream source, OutputStream destination) {
    try {
      copy(source, destination);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
