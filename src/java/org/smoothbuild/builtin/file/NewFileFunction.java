package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;

public class NewFileFunction {
  public static final Charset US_ASCII = Charset.forName("US-ASCII");

  public interface Parameters {
    // TODO params should be marked as @Required
    public String path();

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
      Path filePath = validatedPath("path", params.path(), sandbox);
      if (filePath == null) {
        return null;
      }
      return createFile(filePath);
    }

    private File createFile(Path filePath) {
      MutableFile file = sandbox.createFile(filePath);
      OutputStreamWriter writer = new OutputStreamWriter(file.openOutputStream(), US_ASCII);
      try {
        writer.write(params.content());
        writer.close();
        return file;
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
    }
  }
}
