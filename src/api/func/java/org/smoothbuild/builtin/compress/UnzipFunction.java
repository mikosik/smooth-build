package org.smoothbuild.builtin.compress;

import static java.io.File.createTempFile;
import static okio.Okio.buffer;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import okio.BufferedSource;
import okio.Sink;

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
    try (BufferedSource source = blob.source(); Sink sink = sink(tempFile)) {
      source.readAll(sink);
    }
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
    BlobBuilder builder = nativeApi.create().blobBuilder();
    builder.sink().writeAll(buffer(source(inputStream)));
    return builder.build();
  }
}
