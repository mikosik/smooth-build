package org.smoothbuild.slib.compress;

import static java.io.File.createTempFile;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.record.db.FileStruct.filePath;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.plugin.AbortException;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.util.DuplicatesDetector;

public class UnzipFunction {
  @SmoothFunction("unzip")
  public static Array unzip(NativeApi nativeApi, Blob blob) throws IOException {
    return unzip(nativeApi, blob, x -> true);
  }

  public static Array unzip(NativeApi nativeApi, Blob blob, Predicate<String> filter)
      throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder fileArrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileSpec());
    try {
      File tempFile = copyToTempFile(blob);
      try (ZipFile zipFile = new ZipFile(tempFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          String name = entry.getName();
          if (!name.endsWith("/") && filter.test(name)) {
            Tuple unzippedEntry = unzipEntry(nativeApi, zipFile.getInputStream(entry), entry);
            String fileName = filePath(unzippedEntry).jValue();
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
    copyAllAndClose(blob.source(), sink(tempFile));
    return tempFile;
  }

  private static Tuple unzipEntry(NativeApi nativeApi, InputStream inputStream, ZipEntry entry)
      throws IOException {
    String fileName = entry.getName();
    try {
      path(fileName);
    } catch (IllegalPathException e) {
      nativeApi.log().error("File in archive has illegal name = '" + fileName + "'");
      return null;
    }

    RString path = nativeApi.factory().string(fileName);
    Blob content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
