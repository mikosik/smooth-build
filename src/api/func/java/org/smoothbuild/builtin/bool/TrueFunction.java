package org.smoothbuild.builtin.bool;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;

import okio.BufferedSource;

public class TrueFunction {
  @SmoothFunction("true")
  public static Bool trueFunction(NativeApi nativeApi) throws IOException {
    return nativeApi.create().bool(true);
  }
}
