package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;

public class ZipFunction {

  public interface Parameters {
    /**
     * Files to be zipped.
     */
    public FileList fileList();
  }

  // TODO add missing parameters: level, comment, method

  // TODO investigate zip64 format.
  // Article
  // https://blogs.oracle.com/xuemingshen/entry/zip64_support_for_4g_zipfile
  // suggests that zip64 support has been added in OpenJDK7-b55 which will
  // happen automatically. This way we cannot add 'forbidZip64' param to
  // ZipFunction that is needed when user wants to create non zip64 file
  // or fail the build when it is not possible.
  // Alternative solution is to use apache common-compress
  // http://commons.apache.org/proper/commons-compress/zip.html
  // which provides setUseZip64 method that allows specifying zip64 behaviour.

  @SmoothFunction("zip")
  public static File execute(Sandbox sandbox, Parameters params) throws FunctionException {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    private final byte[] buffer = new byte[1024];

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() throws FunctionException {
      if (params.fileList() == null) {
        throw new MissingArgException("files");
      }
      File output = sandbox.resultFile(path("output.zip"));
      try (ZipOutputStream zipOutputStream = new ZipOutputStream(output.createOutputStream());) {
        for (File file : params.fileList().asIterable()) {
          addEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return output;
    }

    private void addEntry(ZipOutputStream zipOutputStream, File file) throws IOException {
      ZipEntry entry = new ZipEntry(file.path().value());
      zipOutputStream.putNextEntry(entry);

      try (InputStream inputStream = file.createInputStream();) {
        int readCount = inputStream.read(buffer);
        while (readCount > 0) {
          zipOutputStream.write(buffer, 0, readCount);
          readCount = inputStream.read(buffer);
        }
      }

      zipOutputStream.closeEntry();
    }
  }
}
