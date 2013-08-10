package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.function.Param.fileParam;
import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;

@FunctionName("unzip")
public class UnzipFunction implements FunctionDefinition {
  private final Param<File> file = fileParam("file");
  private final Params params = new Params(file);

  private final Files files;
  private final byte[] buffer = new byte[1024];

  public UnzipFunction(Files result) {
    this.files = result;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public Files execute() throws FunctionException {
    if (!file.isSet()) {
      throw new MissingArgException(file);
    }

    try (ZipInputStream zipInputStream = new ZipInputStream(file.get().createInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        unzipEntry(zipInputStream, entry);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }

    return files;
  }

  private void unzipEntry(ZipInputStream zipInputStream, ZipEntry entry) throws IOException {
    File file = files.createFile(path(entry.getName()));
    try (OutputStream outputStream = file.createOutputStream()) {
      int len = 0;
      while ((len = zipInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
  }
}
