package org.smoothbuild.plugin;

import static okio.Okio.source;
import static org.smoothbuild.util.io.Unzip.unzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.util.io.DuplicateFileNameExc;
import org.smoothbuild.util.io.IllegalZipEntryFileNameExc;

import net.lingala.zip4j.exception.ZipException;

public class UnzipBlob {
  public static ArrayB unzipBlob(
      NativeApi nativeApi, BlobB blob, Predicate<String> includePredicate)
      throws IOException, ZipException, DuplicateFileNameExc, IllegalZipEntryFileNameExc {
    var arrayBuilder = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());
    unzip(blob, includePredicate, (f, is) -> arrayBuilder.add(fileB(nativeApi, f, is)));
    return arrayBuilder.build();
  }

  private static TupleB fileB(NativeApi nativeApi, String fileName, InputStream inputStream) {
    StringB path = nativeApi.factory().string(fileName);
    BlobB content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
