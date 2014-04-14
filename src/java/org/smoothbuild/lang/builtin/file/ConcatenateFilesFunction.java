package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateFilesFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SArray<SFile> with();
  }

  @SmoothFunction(name = "concatenateFiles")
  public static SArray<SFile> execute(NativeApi nativeApi, Parameters params) {
    return new Worker(nativeApi, params).execute();
  }

  public static class Worker {
    private final NativeApi nativeApi;
    private final Parameters params;

    public Worker(NativeApi nativeApi, Parameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public SArray<SFile> execute() {
      ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(FILE_ARRAY);

      for (SFile file : params.files()) {
        builder.add(file);
      }
      for (SFile file : params.with()) {
        builder.add(file);
      }

      return builder.build();
    }
  }
}
