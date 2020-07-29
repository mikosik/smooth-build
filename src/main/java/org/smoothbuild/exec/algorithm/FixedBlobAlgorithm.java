package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.compute.Task.NAME_LENGTH_LIMIT;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.spec.BlobSpec;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.plugin.NativeApi;

import okio.ByteString;

public class FixedBlobAlgorithm implements Algorithm {
  private final BlobSpec blobSpec;
  private final ByteString byteString;
  private final String shortedString;

  public FixedBlobAlgorithm(BlobSpec blobSpec, ByteString byteString) {
    this.blobSpec = blobSpec;
    this.byteString = byteString;
    this.shortedString = toStringLimitedWithEllipsis(byteString, NAME_LENGTH_LIMIT);
  }

  private static String toStringLimitedWithEllipsis(ByteString byteString, int limit) {
    String string = "0x" + byteString.toString();
    if (string.length() <= limit) {
      return string;
    } else {
      return string.substring(0, limit - 3) + "...";
    }
  }

  public String shortedLiteral() {
    return shortedString;
  }

  @Override
  public Hash hash() {
    return fixedBlobAlgorithmHash(byteString);
  }

  @Override
  public Spec type() {
    return blobSpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws IOException {
    Blob blob = nativeApi
        .factory()
        .blob(sink -> sink.write(byteString));
    return new Output(blob, nativeApi.messages());
  }
}
