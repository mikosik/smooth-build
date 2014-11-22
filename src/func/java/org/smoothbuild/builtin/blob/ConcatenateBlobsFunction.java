package org.smoothbuild.builtin.blob;

import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateBlobsFunction {

  public interface ConcatenateBlobsParameters {
    @Required
    public Array<Blob> blobs();

    @Required
    public Array<Blob> with();
  }

  @SmoothFunction
  public static Array<Blob> concatenateBlobs(NativeApi nativeApi,
      ConcatenateBlobsParameters params) {
    ArrayBuilder<Blob> builder = nativeApi.arrayBuilder(BLOB_ARRAY);

    for (Blob blob : params.blobs()) {
      builder.add(blob);
    }
    for (Blob blob : params.with()) {
      builder.add(blob);
    }

    return builder.build();
  }
}
