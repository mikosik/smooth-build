package org.smoothbuild.builtin.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.util.DuplicatesDetector;

public class ZipFunction {
  @SmoothFunction
  public static Blob zip(NativeApi nativeApi, Array files, SString javaVersion) {
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = nativeApi.create().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder)) {
      for (Struct file : files.asIterable(Struct.class)) {
        String path = ((SString) file.get("path")).data();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot zip two files with the same path = " + path);
          throw new AbortException();
        }
        zipFile(file, zipOutputStream, buffer);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return blobBuilder.build();
  }

  private static void zipFile(Struct file, ZipOutputStream zipOutputStream, byte[] buffer)
      throws IOException {
    ZipEntry entry = new ZipEntry(((SString) file.get("path")).data());
    zipOutputStream.putNextEntry(entry);
    try (InputStream inputStream = ((Blob) file.get("content")).openInputStream()) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        zipOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }
    zipOutputStream.closeEntry();
  }
}
