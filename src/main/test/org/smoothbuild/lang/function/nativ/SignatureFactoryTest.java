package org.smoothbuild.lang.function.nativ;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.type.Types.FILE;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.SFile;

public class SignatureFactoryTest {

  @Test
  public void test() throws Exception {
    Method method =
        SignatureFactoryTest.class.getMethod("function", NativeApi.class, FuncParams.class);

    Signature signature = SignatureFactory.create(method, FuncParams.class);
    assertEquals(FILE, signature.type());
    assertEquals(name("function"), signature.name());
    assertEquals(asList(optionalParameter(FILE, "param1")), signature.parameters());
  }

  public interface FuncParams {
    public SFile param1();
  }

  @SmoothFunctionLegacy
  public static SFile function(NativeApi nativeApi, FuncParams funcParams) {
    return null;
  }
}
