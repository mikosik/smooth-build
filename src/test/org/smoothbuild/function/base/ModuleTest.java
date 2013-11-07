package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.name;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ModuleTest {
  Name name1 = name("name1");
  Name name2 = name("name2");
  Function function1 = mock(Function.class);
  Function function2 = mock(Function.class);

  Map<Name, Function> map = ImmutableMap.of(name1, function1, name2, function2);

  @Test
  public void getFunction() {
    Module module = new Module(map);

    assertThat(module.getFunction(name1)).isSameAs(function1);
    assertThat(module.getFunction(name2)).isSameAs(function2);
  }

  @Test
  public void nullReturnedWhenFunctionDoesNotExist() throws Exception {
    Module module = new Module(ImmutableMap.<Name, Function> of());
    assertThat(module.getFunction(name1)).isNull();
  }

  @Test
  public void availableNames() throws Exception {
    Module module = new Module(map);
    assertThat(module.availableNames()).containsOnly(name1, name2);
  }
}
