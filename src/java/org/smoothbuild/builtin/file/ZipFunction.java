package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.function.Param.filesParam;
import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;

@FunctionName("zip")
public class ZipFunction implements FunctionDefinition {
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

  private final Param<Files> files = filesParam("files");
  private final Params params = new Params(files);

  private final Files result;
  private final byte[] buffer = new byte[1024];

  public ZipFunction(Files result) {
    this.result = result;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public File execute() throws FunctionException {
    if (!files.isSet()) {
      throw new MissingArgException(files);
    }
    File output = result.createFile(path("output.zip"));
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(output.createOutputStream());) {
      for (File file : files.get().asIterable()) {
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
