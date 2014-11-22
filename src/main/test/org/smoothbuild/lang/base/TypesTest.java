package org.smoothbuild.lang.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Conversions.canConvert;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.NOTHING;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.base.Types.allTypes;
import static org.smoothbuild.lang.base.Types.arrayTypeContaining;
import static org.smoothbuild.lang.base.Types.basicTypes;
import static org.smoothbuild.lang.base.Types.parameterJTypeToType;
import static org.smoothbuild.lang.base.Types.parameterTypes;
import static org.smoothbuild.lang.base.Types.resultJTypeToType;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;

public class TypesTest {

  @Test
  public void basic_types() throws Exception {
    assertThat(basicTypes()).containsExactly(STRING, BLOB, FILE);
  }

  @Test
  public void param_types() throws Exception {
    assertThat(parameterTypes()).containsExactly(STRING, BLOB, FILE, STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY, NIL);
  }

  @Test
  public void all_types() throws Exception {
    assertThat(allTypes()).containsExactly(STRING, BLOB, FILE, NOTHING, STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY, NIL);
  }

  @Test
  public void array_elem_types() throws Exception {
    assertThat(STRING_ARRAY.elemType()).isEqualTo(STRING);
    assertThat(BLOB_ARRAY.elemType()).isEqualTo(BLOB);
    assertThat(FILE_ARRAY.elemType()).isEqualTo(FILE);
    assertThat(NIL.elemType()).isEqualTo(NOTHING);
  }

  @Test
  public void equals_and_hashcode() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(STRING);
    tester.addEqualityGroup(BLOB);
    tester.addEqualityGroup(FILE);
    tester.addEqualityGroup(STRING_ARRAY);
    tester.addEqualityGroup(BLOB_ARRAY);
    tester.addEqualityGroup(FILE_ARRAY);
    tester.addEqualityGroup(NIL);

    tester.testEquals();
  }

  @Test
  public void to_string() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void all_types_returns_list_sorted_by_super_type_dependency() throws Exception {
    Set<Type<?>> visited = Sets.newHashSet();
    for (Type<?> type : allTypes()) {
      for (Type<?> visitedType : visited) {
        assertThat(canConvert(visitedType, type)).isFalse();
      }
      visited.add(type);
    }
  }

  @Test
  public void paramJTypeToType_works_for_all_types() {
    assertThat(parameterJTypeToType(STRING.jType())).isEqualTo(STRING);
    assertThat(parameterJTypeToType(BLOB.jType())).isEqualTo(BLOB);
    assertThat(parameterJTypeToType(FILE.jType())).isEqualTo(FILE);

    assertThat(parameterJTypeToType(STRING_ARRAY.jType())).isEqualTo(STRING_ARRAY);
    assertThat(parameterJTypeToType(BLOB_ARRAY.jType())).isEqualTo(BLOB_ARRAY);
    assertThat(parameterJTypeToType(FILE_ARRAY.jType())).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void resultJTypeToType_works_for_all_types() {
    assertThat(resultJTypeToType(STRING.jType())).isEqualTo(STRING);
    assertThat(resultJTypeToType(BLOB.jType())).isEqualTo(BLOB);
    assertThat(resultJTypeToType(FILE.jType())).isEqualTo(FILE);

    assertThat(resultJTypeToType(STRING_ARRAY.jType())).isEqualTo(STRING_ARRAY);
    assertThat(resultJTypeToType(BLOB_ARRAY.jType())).isEqualTo(BLOB_ARRAY);
    assertThat(resultJTypeToType(FILE_ARRAY.jType())).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void arrayType_containing() throws Exception {
    assertThat(arrayTypeContaining(STRING)).isEqualTo(STRING_ARRAY);
    assertThat(arrayTypeContaining(BLOB)).isEqualTo(BLOB_ARRAY);
    assertThat(arrayTypeContaining(FILE)).isEqualTo(FILE_ARRAY);
    assertThat(arrayTypeContaining(NOTHING)).isEqualTo(NIL);
  }

}
