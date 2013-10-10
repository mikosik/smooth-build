package org.smoothbuild.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.paramsToString;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.testing.function.base.ParamTester.params;

import java.util.Map;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;

import com.google.common.hash.HashCode;

public class ParamTest {
  @Test
  public void creatingParamsMap() throws Exception {
    String name1 = "name1";
    String name2 = "name2";
    Param param1 = param(STRING, name1, false);
    Param param2 = param(STRING, name2, false);

    Map<String, Param> params = params(param1, param2);
    assertThat(params.get(name1)).isSameAs(param1);
    assertThat(params.get(name2)).isSameAs(param2);
  }

  @Test
  public void creatingParamsMapWithDuplicateParamNamesThrowsExcpetion() throws Exception {
    Param param1 = param(STRING, "name");

    try {
      params(param1, param1);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    Param.param(null, "name", true, mock(HashCode.class));
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() throws Exception {
    Param.param(Type.STRING, null, true, mock(HashCode.class));
  }

  @Test(expected = NullPointerException.class)
  public void nullHashCodeIsForbidden() throws Exception {
    Param.param(Type.STRING, "name", true, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void creatingVoidParamIsForbidden() throws Exception {
    Param.param(Type.VOID, "name", true, mock(HashCode.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void creatingEmptySetParamIsForbidden() throws Exception {
    Param.param(Type.EMPTY_SET, "name", true, mock(HashCode.class));
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
  public void hash() throws Exception {
    HashCode hashCode = HashCode.fromInt(33);
    Param param = Param.param(Type.STRING, "name", true, hashCode);
    assertThat(param.hash()).isSameAs(hashCode);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(Param.class)
        .withPrefabValues(HashCode.class, HashCode.fromInt(1), HashCode.fromInt(2))
        .suppress(NULL_FIELDS).verify();
  }

  @Test
  public void toPaddedString() throws Exception {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(STRING);

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName       ");
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(STRING);

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

  private static Param param(Type type, String name) {
    return param(type, name, false);
  }

  private static Param param(Type type, String name, boolean isRequired) {
    HashCode hashCode = mock(HashCode.class);
    return Param.param(type, name, isRequired, hashCode);
  }
}
