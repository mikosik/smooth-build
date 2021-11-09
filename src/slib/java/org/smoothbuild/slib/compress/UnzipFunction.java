package org.smoothbuild.slib.compress;

import static java.io.File.createTempFile;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;

public class UnzipFunction {
  public static ArrayH function(NativeApi nativeApi, BlobH blob) throws IOException {
    try {
      return unzip(nativeApi, blob, x -> true);
    } catch (ZipException e) {
      nativeApi.log().error("Cannot read archive. Corrupted data?");
      return null;
    }
  }

  public static ArrayH unzip(NativeApi nativeApi, BlobH blob, Predicate<String> filter)
      throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    ArrayHBuilder fileArrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());
    File tempFile = copyToTempFile(blob);
    try (ZipFile zipFile = new ZipFile(tempFile)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        if (!name.endsWith("/") && filter.test(name)) {
          TupleH unzippedEntry = unzipEntry(nativeApi, zipFile.getInputStream(entry), entry);
          if (unzippedEntry != null) {
            String fileName = filePath(unzippedEntry).jValue();
            if (duplicatesDetector.addValue(fileName)) {
              nativeApi.log().warning(
                  "Archive contains two files with the same path = " + fileName);
            }
            fileArrayBuilder.add(unzippedEntry);
          }
        }
      }
    }
    return fileArrayBuilder.build();
  }

  private static File copyToTempFile(BlobH blob) throws IOException {
    File tempFile = createTempFile("unzip", null);
    copyAllAndClose(blob.source(), sink(tempFile));
    return tempFile;
  }

  private static TupleH unzipEntry(NativeApi nativeApi, InputStream inputStream, ZipEntry entry) {
    String fileName = entry.getName();
    try {
      path(fileName);
    } catch (IllegalPathException e) {
      nativeApi.log().error("File in archive has illegal name = '" + fileName + "'");
      return null;
    }

    StringH path = nativeApi.factory().string(fileName);
    BlobH content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
