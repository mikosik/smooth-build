package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.MutableFile;
import org.smoothbuild.plugin.MutableFileSet;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    public File file();
  }

  @SmoothFunction("unzip")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
      byte[] buffer = new byte[1024];

      if (params.file() == null) {
        sandbox.report(new MissingRequiredArgError("file"));
      }

      MutableFileSet resultFiles = new MutableFileSet();
      try (ZipInputStream zipInputStream = new ZipInputStream(params.file().createInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          resultFiles.add(unzipEntry(zipInputStream, entry, buffer));
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return resultFiles;
    }

    private File unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, byte[] buffer)
        throws IOException {
      MutableFile file = sandbox.createFile(path(entry.getName()));
      try (OutputStream outputStream = file.createOutputStream()) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return file;
    }
  }
}
