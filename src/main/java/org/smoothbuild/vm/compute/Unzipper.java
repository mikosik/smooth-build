package org.smoothbuild.vm.compute;

import static java.io.File.createTempFile;
import static okio.Okio.buffer;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.io.fs.base.PathS.failIfNotLegalPath;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.io.fs.base.IllegalPathExc;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;
import org.smoothbuild.util.function.ThrowingBiConsumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import okio.ByteString;

public class Unzipper {
  private final NativeApi nativeApi;

  public Unzipper(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
  }

  public ArrayB unzip(BlobB blob, Predicate<String> filter) throws IOException {
    var arrayBuilder = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());
    unzip(blob, filter, (f, is) -> arrayBuilder.add(fileB(nativeApi, f, is)));
    return arrayBuilder.build();
  }

  public ImmutableMap<String, ByteString> unzipToMap(BlobB blob, Predicate<String> filter)
      throws IOException {
    Builder<String, ByteString> builder = ImmutableMap.builder();
    unzip(blob, filter, (f, is) -> builder.put(f, buffer(source(is)).readByteString()));
    return builder.build();
  }

  private void unzip(BlobB blob, Predicate<String> filter,
      ThrowingBiConsumer<String, InputStream, IOException> entryConsumer) throws IOException {
    // We have to use ZipFile (that can only unzip disk files) instead of
    // ZipInputStream (that can unzip in memory stream) because the latter
    // cannot detect corrupted zip-files correctly. Its readLOC() (private) method
    // returns null in case of some errors as if no more zip entries were present
    // while in fact those entries might be corrupted.
    var duplicatesDetector = new DuplicatesDetector<String>();
    var tempFile = copyToTempFile(blob);
    try (var zipFile = new ZipFile(tempFile)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        var zipEntry = entries.nextElement();
        var fileName = zipEntry.getName();
        if (!fileName.endsWith("/") && filter.test(fileName)) {
          try {
            failIfNotLegalPath(fileName);
            entryConsumer.accept(fileName, zipFile.getInputStream(zipEntry));
            if (duplicatesDetector.addValue(fileName)) {
              nativeApi.log().warning(
                  "Archive contains two files with the same path = " + fileName);
            }
          } catch (IllegalPathExc e) {
            nativeApi.log().error(
                "File in archive has illegal name = '" + fileName + "'. " + e.getMessage());
          }
        }
      }
    }
  }

  private static File copyToTempFile(BlobB blob) throws IOException {
    File tempFile = createTempFile("unzip", null);
    copyAllAndClose(blob.source(), sink(tempFile));
    return tempFile;
  }

  private static TupleB fileB(NativeApi nativeApi, String fileName, InputStream inputStream) {
    StringB path = nativeApi.factory().string(fileName);
    BlobB content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
