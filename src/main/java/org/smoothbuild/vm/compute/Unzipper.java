package org.smoothbuild.vm.compute;

import static java.io.File.createTempFile;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.PathS.path;
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

public class Unzipper {
  private final NativeApi nativeApi;

  public Unzipper(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
  }

  public ArrayB unzip(BlobB blob, Predicate<String> filter) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    var fileArrayBuilder = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());
    File tempFile = copyToTempFile(blob);
    try (ZipFile zipFile = new ZipFile(tempFile)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        if (!name.endsWith("/") && filter.test(name)) {
          TupleB unzippedEntry = unzipEntry(nativeApi, zipFile.getInputStream(entry), entry);
          if (unzippedEntry != null) {
            String fileName = filePath(unzippedEntry).toJ();
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

  private static File copyToTempFile(BlobB blob) throws IOException {
    File tempFile = createTempFile("unzip", null);
    copyAllAndClose(blob.source(), sink(tempFile));
    return tempFile;
  }

  private static TupleB unzipEntry(NativeApi nativeApi, InputStream inputStream, ZipEntry entry) {
    String fileName = entry.getName();
    try {
      path(fileName);
    } catch (IllegalPathExc e) {
      nativeApi.log().error("File in archive has illegal name = '" + fileName + "'");
      return null;
    }

    StringB path = nativeApi.factory().string(fileName);
    BlobB content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
