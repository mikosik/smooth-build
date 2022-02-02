package org.smoothbuild.vm.compute;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.io.fs.base.PathS.detectPathError;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;
import org.smoothbuild.util.function.ThrowingBiConsumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import okio.ByteString;

public class Unzipper {
  private final NativeApi nativeApi;

  public Unzipper(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
  }

  public ArrayB unzip(BlobB blob, Predicate<String> includePredicate)
      throws IOException, ZipException {
    var arrayBuilder = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());
    unzip(blob, includePredicate, (f, is) -> arrayBuilder.add(fileB(nativeApi, f, is)));
    return arrayBuilder.build();
  }

  public ImmutableMap<String, ByteString> unzipToMap(BlobB blob, Predicate<String> includePredicate)
      throws IOException, ZipException {
    Builder<String, ByteString> builder = ImmutableMap.builder();
    unzip(blob, includePredicate, (f, is) -> builder.put(f, buffer(source(is)).readByteString()));
    return builder.build();
  }

  private void unzip(BlobB blob, Predicate<String> includePredicate,
      ThrowingBiConsumer<String, InputStream, IOException> entryConsumer)
      throws IOException, ZipException {
    var duplicatesDetector = new DuplicatesDetector<String>();
    try (var s = blob.source(); var zipInputStream = new ZipInputStream(s.inputStream())) {
      LocalFileHeader header;
      while ((header = zipInputStream.getNextEntry()) != null) {
        var fileName = header.getFileName();
        if (!fileName.endsWith("/") && includePredicate.test(fileName)) {
          String pathError = detectPathError(fileName);
          if (pathError != null) {
            nativeApi.log().error(
                "File in archive has illegal name = '" + fileName + "'. " + pathError);
          }
          entryConsumer.accept(fileName, zipInputStream);
          if (duplicatesDetector.addValue(fileName)) {
            nativeApi.log().warning(
                "Archive contains two files with the same path = " + fileName);
          }
        }
      }
    }
  }

  private static TupleB fileB(NativeApi nativeApi, String fileName, InputStream inputStream) {
    StringB path = nativeApi.factory().string(fileName);
    BlobB content = nativeApi.factory().blob(sink -> sink.writeAll(source(inputStream)));
    return nativeApi.factory().file(path, content);
  }
}
