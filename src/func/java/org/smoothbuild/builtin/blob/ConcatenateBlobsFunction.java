package org.smoothbuild.builtin.blob;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;

public class ConcatenateBlobsFunction {

  public interface ConcatenateBlobsParameters {
    @Required
    public Array<Blob> blobs();

    @Required
    public Array<Blob> with();
  }

  @SmoothFunctionLegacy
  public static Array<Blob> concatenateBlobs(NativeApi nativeApi, ConcatenateBlobsParameters params) {
    ArrayBuilder<Blob> builder = nativeApi.arrayBuilder(Blob.class);

    for (Blob blob : params.blobs()) {
      builder.add(blob);
    }
    for (Blob blob : params.with()) {
      builder.add(blob);
    }

    return builder.build();
  }
}
