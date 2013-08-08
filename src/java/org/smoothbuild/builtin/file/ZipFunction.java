package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.function.Param.filesParam;
import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.FileRw;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.lang.type.FilesRw;

@FunctionName("zip")
public class ZipFunction implements Function {
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

  private final Param<FilesRo> files = filesParam("files");
  private final Params params = new Params(files);

  private final FilesRw filesRw;
  private final byte[] buffer = new byte[1024];

  public ZipFunction(FilesRw result) {
    this.filesRw = result;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public FileRo execute() throws FunctionException {
    if (!files.isSet()) {
      throw new MissingArgException(files);
    }
    FileRw result = filesRw.createFileRw(path("output.zip"));
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(result.createOutputStream());) {
      for (FileRo fileRo : files.get().asIterable()) {
        addEntry(zipOutputStream, fileRo);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }

    return result;
  }

  private void addEntry(ZipOutputStream zipOutputStream, FileRo fileRo) throws IOException {
    ZipEntry entry = new ZipEntry(fileRo.path().value());
    zipOutputStream.putNextEntry(entry);

    try (InputStream inputStream = fileRo.createInputStream();) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        zipOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }

    zipOutputStream.closeEntry();
  }
}
