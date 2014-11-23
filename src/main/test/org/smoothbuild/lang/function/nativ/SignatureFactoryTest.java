package org.smoothbuild.lang.function.nativ;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.parameter;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.collect.ImmutableList;

public class SignatureFactoryTest {

  @Test
  public void test() throws Exception {
    Method method =
        SignatureFactoryTest.class.getMethod("function", NativeApi.class, FuncParams.class);

    Signature<?> signature = SignatureFactory.create(method, FuncParams.class);
    assertEquals(FILE, signature.type());
    assertEquals(name("function"), signature.name());
    assertEquals(ImmutableList.of(parameter(FILE, "param1", false)), signature.parameters());
  }

  public interface FuncParams {
    public SFile param1();
  }

  @SmoothFunction
  public static SFile function(NativeApi nativeApi, FuncParams funcParams) {
    return null;
  }
}
