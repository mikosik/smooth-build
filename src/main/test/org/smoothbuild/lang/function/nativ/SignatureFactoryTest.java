package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;

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
    Method method = SignatureFactoryTest.class.getMethod("function", NativeApi.class,
        FuncParams.class);

    Signature<?> signature = SignatureFactory.create(method, FuncParams.class);
    assertThat(signature.type()).isEqualTo(FILE);
    assertThat(signature.name()).isEqualTo(name("function"));
    assertThat(signature.params()).isEqualTo(ImmutableList.of(param(FILE, "param1", false)));
  }

  public interface FuncParams {
    public SFile param1();
  }

  @SmoothFunction
  public static SFile function(NativeApi nativeApi, FuncParams funcParams) {
    return null;
  }
}
