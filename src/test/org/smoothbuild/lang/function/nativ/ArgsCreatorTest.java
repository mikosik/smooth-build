package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.testing.lang.type.FakeString;

import com.google.common.collect.ImmutableMap;

public class ArgsCreatorTest {
  ArgsCreator argsCreator = new ArgsCreator(MyParametersInterface.class);
  String name = "string";
  SString value = new FakeString("value");

  @Test
  public void stringArgumentIsPassed() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, SValue> of(name, value));

    assertThat(args.string()).isEqualTo(value);
  }

  @Test
  public void nullReturnedForNotArrayArguments() {
    MyParametersInterface args = createArgs(ImmutableMap.<String, SValue> of(name, value));

    assertThat(args.integer()).isNull();
    assertThat(args.string2()).isNull();
  }

  private MyParametersInterface createArgs(ImmutableMap<String, SValue> map) {
    return (MyParametersInterface) argsCreator.create(map);
  }

  public interface MyParametersInterface {
    public SString string();

    public SString string2();

    public Integer integer();
  }
}
