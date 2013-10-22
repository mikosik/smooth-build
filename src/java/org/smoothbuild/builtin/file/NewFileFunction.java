package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.type.api.File;

public class NewFileFunction {
  public static final Charset US_ASCII = Charset.forName("US-ASCII");

  public interface Parameters {
    @Required
    public String path();

    @Required
    public String content();
  }

  @SmoothFunction("newFile")
  public static File execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      return createFile(validatedPath("path", params.path()));
    }

    private File createFile(Path filePath) {
      FileBuilder fileBuilder = sandbox.fileBuilder();
      fileBuilder.setPath(filePath);

      OutputStreamWriter writer = new OutputStreamWriter(fileBuilder.openOutputStream(), US_ASCII);
      try {
        writer.write(params.content());
        writer.close();
        return fileBuilder.build();
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
    }
  }
}
