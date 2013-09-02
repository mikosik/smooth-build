package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;

public class UnzipFunction {
  public interface Parameters {
    public File file();
  }

  private final Sandbox sandbox;

  public UnzipFunction(Sandbox sandbox) {
    this.sandbox = sandbox;
  }

  @SmoothFunction("unzip")
  public FileList execute(Parameters params) throws FunctionException {
    byte[] buffer = new byte[1024];

    if (params.file() == null) {
      throw new MissingArgException("file");
    }

    FileList resultFiles = sandbox.resultFileList();
    try (ZipInputStream zipInputStream = new ZipInputStream(params.file().createInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        unzipEntry(zipInputStream, entry, resultFiles, buffer);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }

    return resultFiles;
  }

  private void unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, FileList fileList,
      byte[] buffer) throws IOException {
    File file = fileList.createFile(path(entry.getName()));
    try (OutputStream outputStream = file.createOutputStream()) {
      int len = 0;
      while ((len = zipInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
  }
}
