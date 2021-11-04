package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.type.val.BlobTypeO;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import okio.ByteString;

public class FixedBlobAlgorithm extends Algorithm {
  private final ByteString byteString;
  private final String shortedString;

  public FixedBlobAlgorithm(BlobTypeO blobType, ByteString byteString) {
    super(blobType);
    this.byteString = byteString;
    this.shortedString = toStringLimitedWithEllipsis(byteString, NAME_LENGTH_LIMIT);
  }

  private static String toStringLimitedWithEllipsis(ByteString byteString, int limit) {
    String string = "0x" + byteString.hex();
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
  public Output run(Input input, NativeApi nativeApi) {
    Blob blob = nativeApi
        .factory()
        .blob(sink -> sink.write(byteString));
    return new Output(blob, nativeApi.messages());
  }
}
