package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.name;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ImmutableModuleTest {
  Name name1 = name("name1");
  Name name2 = name("name2");
  Function function1 = mock(Function.class);
  Function function2 = mock(Function.class);

  Map<Name, Function> map = ImmutableMap.of(name1, function1, name2, function2);

  @Test
  public void getFunction() {
    Module immutableModule = new ImmutableModule(map);

    assertThat(immutableModule.getFunction(name1)).isSameAs(function1);
    assertThat(immutableModule.getFunction(name2)).isSameAs(function2);
  }

  @Test
  public void nullReturnedWhenFunctionDoesNotExist() throws Exception {
    Module immutableModule = new ImmutableModule(ImmutableMap.<Name, Function> of());
    assertThat(immutableModule.getFunction(name1)).isNull();
  }

  @Test
  public void availableNames() throws Exception {
    Module immutableModule = new ImmutableModule(map);
    assertThat(immutableModule.availableNames()).containsOnly(name1, name2);
  }
}
