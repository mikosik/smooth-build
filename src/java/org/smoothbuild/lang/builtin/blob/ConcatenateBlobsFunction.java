package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;

public class ConcatenateBlobsFunction {

  public interface Parameters {
    @Required
    public SArray<SBlob> blobs();

    @Required
    public SArray<SBlob> with();
  }

  @SmoothFunction(name = "concatenateBlobs")
  public static SArray<SBlob> execute(PluginApi pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  public static class Worker {
    private final PluginApi pluginApi;
    private final Parameters params;

    public Worker(PluginApi pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SArray<SBlob> execute() {
      ArrayBuilder<SBlob> builder = pluginApi.arrayBuilder(BLOB_ARRAY);

      for (SBlob blob : params.blobs()) {
        builder.add(blob);
      }
      for (SBlob blob : params.with()) {
        builder.add(blob);
      }

      return builder.build();
    }
  }
}
