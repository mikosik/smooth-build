package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.task.exec.PluginApiImpl;

public class NewFileFunction {
  public static final Charset US_ASCII = Charset.forName("US-ASCII");

  public interface Parameters {
    @Required
    public SString path();

    @Required
    public SString content();
  }

  @SmoothFunction(name = "newFile")
  public static SFile execute(PluginApiImpl pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private final PluginApi pluginApi;
    private final Parameters params;

    public Worker(PluginApi pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SFile execute() {
      return createFile(validatedPath("path", params.path()));
    }

    private SFile createFile(Path filePath) {
      FileBuilder fileBuilder = pluginApi.fileBuilder();
      fileBuilder.setPath(filePath);
      fileBuilder.setContent(createContent(params.content()));
      return fileBuilder.build();
    }

    private SBlob createContent(SString content) {
      BlobBuilder contentBuilder = pluginApi.blobBuilder();
      OutputStream outputStream = contentBuilder.openOutputStream();
      try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, CHARSET)) {
        writer.write(content.value());
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
      return contentBuilder.build();
    }
  }
}
