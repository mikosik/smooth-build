package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.util.Streams.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.task.exec.PluginApiImpl;

public class FileReader {
  private final PluginApiImpl pluginApi;

  public FileReader(PluginApiImpl pluginApi) {
    this.pluginApi = pluginApi;
  }

  public SFile createFile(Path path, Path projectPath) {
    FileBuilder fileBuilder = pluginApi.fileBuilder();
    fileBuilder.setPath(path);
    fileBuilder.setContent(createContent(projectPath));
    return fileBuilder.build();
  }

  private SBlob createContent(Path path) {
    InputStream inputStream = pluginApi.projectFileSystem().openInputStream(path);
    BlobBuilder contentBuilder = pluginApi.blobBuilder();
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
