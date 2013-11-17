package org.smoothbuild.lang.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.STRING;

import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;
import org.smoothbuild.lang.function.def.Node;

import com.google.common.hash.HashCode;

public class ParamTest {

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    Param.param(null, "name", true);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() throws Exception {
    Param.param(Type.STRING, null, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void creatingEmptySetParamIsForbidden() throws Exception {
    Param.param(Type.EMPTY_SET, "name", true);
  }

  @Test
  public void type() throws Exception {
    assertThat(param(Type.STRING, "name", true).type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() throws Exception {
    assertThat(param(Type.STRING, "name", true).name()).isEqualTo("name");
  }

  @Test
  public void isRequired() throws Exception {
    assertThat(param(Type.STRING, "name", true).isRequired()).isTrue();
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(Param.class)
        .withPrefabValues(HashCode.class, HashCode.fromInt(1), HashCode.fromInt(2))
        .suppress(NULL_FIELDS).verify();
  }

  @Test
  public void toPaddedString() throws Exception {
    Node abstractNode = mock(Node.class);
    when(abstractNode.type()).thenReturn(STRING);

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName       ");
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    Node abstractNode = mock(Node.class);
    when(abstractNode.type()).thenReturn(STRING);

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(1, 1);

    assertThat(actual).isEqualTo("String: myName");
  }

  @Test
  public void testToString() throws Exception {
    assertThat(param(Type.STRING, "name", false).toString()).isEqualTo("Param(String: name)");
  }

  @Test
  public void testParamsToString() throws Exception {
    Set<Param> params = newHashSet();
    params.add(param(STRING, "param1", false));
    params.add(param(STRING, "param2-with-very-long", false));
    params.add(param(FILE_SET, "param3", true));

    String actual = paramsToString(params);

    StringBuilder builder = new StringBuilder();
    builder.append("  String: param1               \n");
    builder.append("  File* : param3               \n");
    builder.append("  String: param2-with-very-long\n");
    assertThat(actual).isEqualTo(builder.toString());
  }
}
