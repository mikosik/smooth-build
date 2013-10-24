package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.testing.function.base.ParamTester.params;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

public class SignatureFactoryTest {

  @Test
  public void test() throws Exception {
    Method method = SignatureFactoryTest.class.getMethod("smoothMethod", Sandbox.class,
        Params.class);

    Signature signature = SignatureFactory.create(method, Params.class);
    assertThat(signature.type()).isEqualTo(FILE);
    assertThat(signature.name()).isEqualTo(simpleName("function"));
    assertThat(signature.params()).isEqualTo(params(param(FILE, "param1", false)));
  }

  public interface Params {
    public File param1();
  }

  @SmoothFunction("function")
  public static File smoothMethod(Sandbox sandbox, Params params) {
    return null;
  }
}
