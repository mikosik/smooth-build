package org.smoothbuild.builtin.java;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction("unjar")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;
    byte[] buffer;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
      this.buffer = new byte[1024];
    }

    public FileSet execute() {
      MutableFileSet resultFiles = sandbox.resultFileSet();
      try (JarInputStream jarInputStream = new JarInputStream(params.file().openInputStream());) {
        JarEntry entry = null;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          unjarEntry(jarInputStream, entry, resultFiles);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return resultFiles;
    }

    private File unjarEntry(JarInputStream jarInputStream, JarEntry entry,
        MutableFileSet resultFiles) throws IOException {
      MutableFile file = resultFiles.createFile(path(entry.getName()));
      try (OutputStream outputStream = file.openOutputStream()) {
        int len = 0;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return file;
    }
  }
}
