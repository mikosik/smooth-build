package org.smoothbuild.builtin.compress;

import static java.io.File.createTempFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Streams.copy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.util.DuplicatesDetector;

public class UnzipFunction {
  @SmoothFunction
  public static Array unzip(NativeApi nativeApi, Blob blob, Array javaHash) throws IOException {
    return unzip(nativeApi, blob, x -> true);
  }

  public static Array unzip(NativeApi nativeApi, Blob blob, Predicate<String> filter)
      throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder fileArrayBuilder = nativeApi.create().arrayBuilder(nativeApi.types().file());
    try {
      File tempFile = copyToTempFile(blob);
      try (ZipFile zipFile = new ZipFile(tempFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          String name = entry.getName();
          if (!name.endsWith("/") && filter.test(name)) {
            Struct unzippedEntry = unzipEntry(nativeApi, zipFile.getInputStream(entry), entry);
            String fileName = ((SString) unzippedEntry.get("path")).data();
            if (duplicatesDetector.addValue(fileName)) {
              nativeApi.log().error("archive contains two files with the same path = " + fileName);
              throw new AbortException();
            }

            fileArrayBuilder.add(unzippedEntry);
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (ZipException e) {
      nativeApi.log().error("Cannot read archive. Corrupted data?");
      throw new AbortException();
    }
  }

  private static File copyToTempFile(Blob blob) throws IOException {
    File tempFile = createTempFile("unzip", null);
    copy(blob.openInputStream(), new BufferedOutputStream(new FileOutputStream(tempFile)));
    return tempFile;
  }

  private static Struct unzipEntry(NativeApi nativeApi, InputStream inputStream, ZipEntry entry)
      throws IOException {
    String fileName = entry.getName();
    try {
      path(fileName);
    } catch (IllegalPathException e) {
      nativeApi.log().error("File in archive has illegal name = '" + fileName + "'");
      return null;
    }

    SString path = nativeApi.create().string(fileName);
    Blob content = unzipEntryContent(nativeApi, inputStream);
    return nativeApi.create().file(path, content);
  }

  private static Blob unzipEntryContent(NativeApi nativeApi, InputStream inputStream)
      throws IOException {
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    BlobBuilder contentBuilder = nativeApi.create().blobBuilder();
    try (OutputStream outputStream = contentBuilder) {
      int len;
      while ((len = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
    return contentBuilder.build();
  }
}
