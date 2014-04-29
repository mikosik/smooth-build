package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.util.Streams.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FileReader {
  private final NativeApiImpl nativeApi;

  public FileReader(NativeApiImpl nativeApi) {
    this.nativeApi = nativeApi;
  }

  public SFile createFile(Path path, Path projectPath) {
    return nativeApi.file(path, createContent(projectPath));
  }

  private SBlob createContent(Path path) {
    InputStream inputStream = nativeApi.projectFileSystem().openInputStream(path);
    BlobBuilder contentBuilder = nativeApi.blobBuilder();
    doCopy(inputStream, contentBuilder.openOutputStream());
    return contentBuilder.build();
  }

  private static void doCopy(InputStream source, OutputStream destination) {
    try {
      copy(source, destination);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
