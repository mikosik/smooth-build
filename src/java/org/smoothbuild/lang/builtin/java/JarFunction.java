package org.smoothbuild.lang.builtin.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.Constants;
import org.smoothbuild.lang.builtin.java.err.CannotAddDuplicatePathError;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    public SBlob manifest();
  }

  @SmoothFunction(name = "jar")
  public static SBlob execute(PluginApi pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private final PluginApi pluginApi;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<Path> duplicatesDetector;

    public Worker(PluginApi pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
      this.duplicatesDetector = new DuplicatesDetector<Path>();
    }

    public SBlob execute() {
      BlobBuilder blobBuilder = pluginApi.blobBuilder();
      try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder);) {
        for (SFile file : params.files()) {
          addEntry(jarOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return blobBuilder.build();
    }

    private JarOutputStream createOutputStream(BlobBuilder blobBuilder) throws IOException {
      OutputStream outputStream = blobBuilder.openOutputStream();
      if (params.manifest() == null) {
        return new JarOutputStream(outputStream);
      } else {
        Manifest manifest = new Manifest(params.manifest().openInputStream());
        return new JarOutputStream(outputStream, manifest);
      }
    }

    private void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
      Path path = file.path();
      if (duplicatesDetector.add(path)) {
        throw new ErrorMessageException(new CannotAddDuplicatePathError(path));
      }
      JarEntry entry = new JarEntry(path.value());
      jarOutputStream.putNextEntry(entry);

      try (InputStream inputStream = file.content().openInputStream();) {
        int readCount = inputStream.read(buffer);
        while (readCount > 0) {
          jarOutputStream.write(buffer, 0, readCount);
          readCount = inputStream.read(buffer);
        }
      }

      jarOutputStream.closeEntry();
    }
  }
}
