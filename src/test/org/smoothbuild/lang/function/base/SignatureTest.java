package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.STRING;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SignatureTest {
  Type type = Type.STRING;
  Name name = name("name");
  ImmutableList<Param> params = ImmutableList.of();

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() {
    new Signature(null, name, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() {
    new Signature(type, null, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullParamsIsForbidden() {
    new Signature(type, name, null);
  }

  @Test
  public void paramsAreSortedAccordingToName() throws Exception {
    String name1 = "aaa";
    String name2 = "bbb";
    String name3 = "ccc";
    String name4 = "ddd";
    String name5 = "eee";
    String name6 = "fff";
    Param param1 = param(STRING, name1, false);
    Param param2 = param(STRING, name2, false);
    Param param3 = param(STRING, name3, false);
    Param param4 = param(STRING, name4, false);
    Param param5 = param(STRING, name5, false);
    Param param6 = param(STRING, name6, false);

    Signature signature = new Signature(type, name, ImmutableList.of(param4, param6, param1,
        param3, param5, param2));

    ImmutableMap<String, Param> map = signature.params();
    assertThat(map.values()).containsExactly(param1, param2, param3, param4, param5, param6);
    assertThat(map.keySet()).containsExactly(name1, name2, name3, name4, name5, name6);
  }
}
