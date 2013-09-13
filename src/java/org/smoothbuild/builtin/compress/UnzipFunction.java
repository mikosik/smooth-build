package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction("unzip")
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
      try (ZipInputStream zipInputStream = new ZipInputStream(params.file().openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          unzipEntry(zipInputStream, entry, resultFiles);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return resultFiles;
    }

    private File unzipEntry(ZipInputStream zipInputStream, ZipEntry entry,
        MutableFileSet resultFiles) throws IOException {
      MutableFile file = resultFiles.createFile(path(entry.getName()));
      try (OutputStream outputStream = file.openOutputStream()) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return file;
    }
  }
}
