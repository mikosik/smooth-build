package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.function.base.Params;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class SignatureFactoryTest {

  @Test
  public void test() throws Exception {
    Method method =
        SignatureFactoryTest.class.getMethod("smoothMethod", NativeApi.class, FuncParams.class);

    Signature<?> signature = SignatureFactory.create(method, FuncParams.class);
    assertThat(signature.type()).isEqualTo(FILE);
    assertThat(signature.name()).isEqualTo(name("function"));
    assertThat(signature.params()).isEqualTo(Params.map(param(FILE, "param1", false)));
  }

  public interface FuncParams {
    public SFile param1();
  }

  @SmoothFunction(name = "function")
  public static SFile smoothMethod(NativeApi nativeApi, FuncParams funcParams) {
    return null;
  }
}
