package org.smoothbuild.lang.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.smoothbuild.lang.type.Type.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING;

import java.util.Set;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.Type;

import com.google.common.testing.EqualsTester;

public class ParamTest {

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    param(null, "name", true);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() throws Exception {
    param(STRING, null, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void creatingEmptyArrayParamIsForbidden() throws Exception {
    param(EMPTY_ARRAY, "name", true);
  }

  @Test
  public void type() throws Exception {
    assertThat(param(STRING, "name", true).type()).isEqualTo(STRING);
  }

  @Test
  public void name() throws Exception {
    assertThat(param(STRING, "name", true).name()).isEqualTo("name");
  }

  @Test
  public void isRequired() throws Exception {
    assertThat(param(STRING, "name", true).isRequired()).isTrue();
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(param(STRING, "equal", false), param(STRING, "equal", false));

    for (Type<?> type : Type.allowedForParam()) {
      tester.addEqualityGroup(param(type, "name", false));
      tester.addEqualityGroup(param(type, "name", true));
      tester.addEqualityGroup(param(type, "name2", false));
      tester.addEqualityGroup(param(type, "name2", true));
    }

    tester.testEquals();
  }

  @Test
  public void toPaddedString() throws Exception {
    Node abstractNode = mock(Node.class);
    BDDMockito.willReturn(STRING).given(abstractNode).type();

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName       ");
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    Node abstractNode = mock(Node.class);
    BDDMockito.willReturn(STRING).given(abstractNode).type();

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(1, 1);

    assertThat(actual).isEqualTo("String: myName");
  }

  @Test
  public void testToString() throws Exception {
    assertThat(param(STRING, "name", false).toString()).isEqualTo("Param(String: name)");
  }

  @Test
  public void testParamsToString() throws Exception {
    Set<Param> params = newHashSet();
    params.add(param(STRING, "param1", false));
    params.add(param(STRING, "param2-with-very-long", false));
    params.add(param(FILE_ARRAY, "param3", true));

    String actual = paramsToString(params);

    StringBuilder builder = new StringBuilder();
    builder.append("  String: param1               \n");
    builder.append("  File* : param3               \n");
    builder.append("  String: param2-with-very-long\n");
    assertThat(actual).isEqualTo(builder.toString());
  }
}
