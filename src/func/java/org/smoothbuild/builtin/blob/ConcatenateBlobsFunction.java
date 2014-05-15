package org.smoothbuild.builtin.blob;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateBlobsFunction {

  public interface ConcatenateBlobsParameters {
    @Required
    public SArray<SBlob> blobs();

    @Required
    public SArray<SBlob> with();
  }

  @SmoothFunction(name = "concatenateBlobs")
  public static SArray<SBlob> execute(NativeApi nativeApi, ConcatenateBlobsParameters params) {
    ArrayBuilder<SBlob> builder = nativeApi.arrayBuilder(BLOB_ARRAY);

    for (SBlob blob : params.blobs()) {
      builder.add(blob);
    }
    for (SBlob blob : params.with()) {
      builder.add(blob);
    }

    return builder.build();
  }
}
