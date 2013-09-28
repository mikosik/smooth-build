package org.smoothbuild.builtin.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.err.DuplicatePathError;
import org.smoothbuild.task.err.FileSystemError;

import com.google.common.io.ByteStreams;

public class MergeFunction {

  public interface Parameters {
    @Required
    public FileSet a();

    @Required
    public FileSet b();
  }

  @SmoothFunction("merge")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  public static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
      MutableFileSet result = sandbox.resultFileSet();

      for (File file : params.a()) {
        MutableFile destination = result.createFile(file.path());
        copy(file, destination);
      }
      for (File file : params.b()) {
        MutableFile destination = result.createFile(file.path());
        if (result.contains(file.path())) {
          sandbox.report(new DuplicatePathError(file.path()));
        }
        copy(file, destination);
      }

      return result;
    }

    private static void copy(File file, MutableFile to) {
      try (InputStream is = file.openInputStream(); OutputStream os = to.openOutputStream();) {
        ByteStreams.copy(is, os);
      } catch (IOException e) {
        throw new FileSystemError(e);
      }
    }
  }
}
