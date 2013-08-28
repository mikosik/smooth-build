package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.qualifiedName;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.def.DefinedFunction;

import com.google.common.collect.ImmutableMap;

public class ModuleTest {
  Name name1 = qualifiedName("name1");
  Name name2 = qualifiedName("name2");
  DefinedFunction function1 = mock(DefinedFunction.class);
  DefinedFunction function2 = mock(DefinedFunction.class);

  Map<Name, DefinedFunction> map = ImmutableMap.of(name1, function1, name2, function2);

  @Test
  public void getFunction() {
    Module module = new Module(map);

    assertThat(module.getFunction(name1)).isSameAs(function1);
    assertThat(module.getFunction(name2)).isSameAs(function2);
  }

  @Test
  public void nullReturnedWhenFunctionDoesNotExist() throws Exception {
    Module module = new Module(ImmutableMap.<Name, DefinedFunction> of());
    assertThat(module.getFunction(name1)).isNull();
  }

  @Test
  public void availableNames() throws Exception {
    Module module = new Module(map);
    assertThat(module.availableNames()).containsOnly(name1, name2);
  }
}
