package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.File;

public class SignatureFactoryTest {

  @Test
  public void test() throws Exception {
    Method method = SignatureFactoryTest.class.getMethod("smoothMethod", Sandbox.class,
        Params.class);

    Signature signature = SignatureFactory.create(method, Params.class);
    assertThat(signature.type()).isEqualTo(FILE);
    assertThat(signature.name()).isEqualTo(name("function"));
    assertThat(signature.params()).isEqualTo(params(param(FILE, "param1", false)));
  }

  public interface Params {
    public File param1();
  }

  @SmoothFunction(name = "function")
  public static File smoothMethod(Sandbox sandbox, Params params) {
    return null;
  }
}
