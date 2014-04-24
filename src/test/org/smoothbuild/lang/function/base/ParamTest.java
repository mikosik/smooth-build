package org.smoothbuild.lang.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.allowedForParam;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.util.LineBuilder;

import com.google.common.hash.HashCode;
import com.google.common.testing.EqualsTester;

public class ParamTest {

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() throws Exception {
    param(null, "name", true);
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() throws Exception {
    param(STRING, null, true);
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
  public void is_required() throws Exception {
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
  public void equals_and_hash_code() throws Exception {
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
  public void to_padded_string() throws Exception {
    Expr<?> abstractNode = mock(Expr.class);
    given(willReturn(STRING), abstractNode).type();

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() throws Exception {
    Expr<?> abstractNode = mock(Expr.class);
    given(willReturn(STRING), abstractNode).type();

    Param param = param(STRING, "myName", false);
    String actual = param.toPaddedString(1, 1);

    assertThat(actual).isEqualTo("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    assertThat(param(STRING, "name", false).toString()).isEqualTo("Param(String: name)");
  }

  @Test
  public void params_to_string() throws Exception {
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
