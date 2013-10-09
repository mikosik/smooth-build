package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.type.api.File;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

public class SignatureFactoryTest {
  HashFunction hashFunction = mock(HashFunction.class);
  SignatureFactory signatureFactory = new SignatureFactory(hashFunction);

  @Test
  public void test() throws Exception {
    String name = "param1";
    HashCode hashCode = HashCode.fromInt(33);
    Method method = SignatureFactoryTest.class.getMethod("smoothMethod", Sandbox.class,
        Params.class);

    when(hashFunction.hashString(name, Charsets.UTF_8)).thenReturn(hashCode);
    Signature signature = signatureFactory.create(method, Params.class);
    assertThat(signature.params().get(name).hash()).isSameAs(hashCode);
  }

  public interface Params {
    public File param1();
  }

  @SmoothFunction("function")
  public static File smoothMethod(Sandbox sandbox, Params params) {
    return null;
  }
}
