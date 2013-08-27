package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ArgumentsCreatorTest {
  ArgumentsCreator argumentsCreator = new ArgumentsCreator(MyParametersInterface.class);

  @Test
  public void stringArgumentIsPassed() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, Object> of("string", "value"));

    assertThat(args.string()).isEqualTo("value");
  }

  @Test
  public void nullReturnedForNotSetArguments() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, Object> of("string", "value"));

    assertThat(args.integer()).isNull();
    assertThat(args.string2()).isNull();
  }

  private MyParametersInterface createArgs(ImmutableMap<String, Object> map) {
    return (MyParametersInterface) argumentsCreator.create(map);
  }

  public interface MyParametersInterface {
    public String string();

    public String string2();

    public Integer integer();
  }
}
