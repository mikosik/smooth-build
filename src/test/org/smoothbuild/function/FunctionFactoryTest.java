package org.smoothbuild.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.FullyQualifiedName.fullyQualifiedName;
import static org.smoothbuild.function.Param.param;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.mem.InMemoryFileSystemModule;
import org.smoothbuild.plugin.ExecuteMethod;
import org.smoothbuild.plugin.FunctionName;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

public class FunctionFactoryTest {
  FunctionFactory functionFactory;

  @Before
  public void before() {
    functionFactory = Guice.createInjector(new InMemoryFileSystemModule()).getInstance(
        FunctionFactory.class);
  }

  @Test
  public void testSignature() throws Exception {
    Function function = functionFactory.create(MyFunctionImplementation.class);

    assertThat(function.name()).isEqualTo(fullyQualifiedName("my.package.myFunction"));
    FunctionSignature signature = function.signature();
    assertThat(signature.name()).isEqualTo(fullyQualifiedName("my.package.myFunction"));
    assertThat(signature.type()).isEqualTo(Type.STRING);

    assertThat(signature.params().param("stringA")).isEqualTo(param(Type.STRING, "stringA"));
    assertThat(signature.params().param("stringB")).isEqualTo(param(Type.STRING, "stringB"));
  }

  @Test
  public void testInvokation() throws Exception {
    Function function = functionFactory.create(MyFunctionImplementation.class);
    ImmutableMap<String, Object> args = ImmutableMap.<String, Object> of("stringA", "abc",
        "stringB", "def");
    assertThat(function.execute(path("any/path"), args)).isEqualTo("abcdef");
  }

  public interface Parameters {
    public String stringA();

    public String stringB();
  }

  @FunctionName("my.package.myFunction")
  public static class MyFunctionImplementation {

    @ExecuteMethod
    public String execute(Parameters params) {
      return params.stringA() + params.stringB();
    }
  }
}
