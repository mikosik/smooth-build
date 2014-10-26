package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.ImmutableMap;

public class InvokerTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApi nativeApi = mock(NativeApi.class);

  @Test(expected = NullPointerException.class)
  public void null_method_is_forbidden() throws Exception {
    new Invoker<>(null, mock(ArgsCreator.class));
  }

  @Test(expected = NullPointerException.class)
  public void null_ArgumentsCreator_is_forbidden() throws Exception {
    new Invoker<>(InvokerTest.class.getMethod("null_ArgumentsCreator_is_forbidden"), null);
  }

  @Test
  public void test() throws Exception {
    SString value = objectsDb.string("stringParamValue");
    Method method = InvokerTest.class.getMethod("myMethod", NativeApi.class, Parameters.class);

    Invoker<?> invoker = new Invoker<>(method, new ArgsCreator(Parameters.class));
    ImmutableMap<String, Value> valuesMap = ImmutableMap.<String, Value> of("stringParam", value);
    Object result = invoker.invoke(nativeApi, valuesMap);

    assertThat(result).isSameAs(value);
  }

  public interface Parameters {
    public SString stringParam();
  }

  public static SString myMethod(NativeApi nativeApi, Parameters params) {
    return params.stringParam();
  }
}
