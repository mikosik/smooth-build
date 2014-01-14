package org.smoothbuild.lang.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.allowedForParam;

import java.util.Set;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.util.LineBuilder;

import com.google.common.hash.HashCode;
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
  public void params_with_different_names_have_different_name_hashes() throws Exception {
    HashCode hash1 = param(STRING, "name1", true).nameHash();
    HashCode hash2 = param(STRING, "name2", true).nameHash();
    assertThat(hash1).isNotEqualTo(hash2);
  }

  @Test
  public void params_with_same_names_but_different_types_have_the_same_name_hashes()
      throws Exception {
    HashCode hash1 = param(STRING, "name1", true).nameHash();
    HashCode hash2 = param(BLOB, "name1", true).nameHash();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(param(STRING, "equal", false), param(STRING, "equal", false));

    for (SType<?> type : allowedForParam()) {
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

    LineBuilder builder = new LineBuilder();
    builder.addLine("  String: param1               ");
    builder.addLine("  File[]: param3               ");
    builder.addLine("  String: param2-with-very-long");
    assertThat(actual).isEqualTo(builder.build());
  }
}
