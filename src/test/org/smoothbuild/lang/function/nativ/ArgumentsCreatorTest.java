package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.lang.function.value.Value;
import org.smoothbuild.testing.lang.function.value.FakeString;

import com.google.common.collect.ImmutableMap;

public class ArgumentsCreatorTest {
  ArgumentsCreator argumentsCreator = new ArgumentsCreator(MyParametersInterface.class);
  String name = "string";
  StringValue value = new FakeString("value");

  @Test
  public void stringArgumentIsPassed() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, Value> of(name, value));

    assertThat(args.string()).isEqualTo(value);
  }

  @Test
  public void nullReturnedForNotSetArguments() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, Value> of(name, value));

    assertThat(args.integer()).isNull();
    assertThat(args.string2()).isNull();
  }

  private MyParametersInterface createArgs(ImmutableMap<String, Value> map) {
    return (MyParametersInterface) argumentsCreator.create(map);
  }

  public interface MyParametersInterface {
    public StringValue string();

    public StringValue string2();

    public Integer integer();
  }
}
