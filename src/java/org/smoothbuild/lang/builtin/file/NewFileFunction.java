package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
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
