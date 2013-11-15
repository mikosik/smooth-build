package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.SandboxImpl;

public class NewFileFunction {
  public static final Charset US_ASCII = Charset.forName("US-ASCII");

  public interface Parameters {
    @Required
    public StringValue path();

    @Required
    public StringValue content();
  }

  @SmoothFunction(name = "newFile")
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

      OutputStream outputStream = fileBuilder.openOutputStream();
      try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, CHARSET)) {
        writer.write(params.content().value());
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
      return fileBuilder.build();
    }
  }
}
