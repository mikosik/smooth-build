package org.smoothbuild.builtin.file;

import static org.smoothbuild.util.Streams.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.task.exec.Container;

import okio.BufferedSource;

public class FileReader {
  private final Container container;

  public FileReader(Container container) {
    this.container = container;
  }

  public Struct createFile(Path path, Path projectPath) throws IOException {
    return container.create().file(createPath(path), createContent(projectPath));
  }

  private SString createPath(Path path) {
    return container.create().string(path.value());
  }

  private Blob createContent(Path path) throws IOException {
    BufferedSource source = container.fileSystem().source(path);
    BlobBuilder contentBuilder = container.create().blobBuilder();
    doCopy(source.inputStream(), contentBuilder);
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
