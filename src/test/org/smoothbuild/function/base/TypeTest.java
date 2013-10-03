package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.function.base.Type.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.function.base.Type.PARAM_TYPES;
import static org.smoothbuild.function.base.Type.RESULT_TYPES;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.function.base.Type.javaParamTypetoType;
import static org.smoothbuild.function.base.Type.javaResultTypetoType;

import org.junit.Test;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.api.StringSet;
import org.smoothbuild.type.impl.EmptySet;

import com.google.inject.TypeLiteral;

public class TypeTest {

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(STRING.isAssignableFrom(VOID)).isFalse();
    assertThat(STRING.isAssignableFrom(EMPTY_SET)).isFalse();

    assertThat(STRING_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(STRING_SET)).isTrue();
    assertThat(STRING_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(VOID)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(EMPTY_SET)).isTrue();

    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(FILE.isAssignableFrom(VOID)).isFalse();
    assertThat(FILE.isAssignableFrom(EMPTY_SET)).isFalse();

    assertThat(FILE_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE_SET)).isTrue();
    assertThat(FILE_SET.isAssignableFrom(VOID)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(EMPTY_SET)).isTrue();

    assertThat(EMPTY_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(VOID)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(EMPTY_SET)).isTrue();

    assertThat(VOID.isAssignableFrom(STRING)).isFalse();
    assertThat(VOID.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(VOID.isAssignableFrom(FILE)).isFalse();
    assertThat(VOID.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(VOID.isAssignableFrom(VOID)).isTrue();
    assertThat(VOID.isAssignableFrom(EMPTY_SET)).isFalse();
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    assertThat(STRING).isEqualTo(STRING);
    assertThat(STRING).isNotEqualTo(FILE);
    assertThat(STRING).isNotEqualTo(FILE_SET);

    assertThat(FILE).isNotEqualTo(STRING);
    assertThat(FILE).isEqualTo(FILE);
    assertThat(FILE).isNotEqualTo(FILE_SET);

    assertThat(FILE_SET).isNotEqualTo(STRING);
    assertThat(FILE_SET).isNotEqualTo(FILE);
    assertThat(FILE_SET).isEqualTo(FILE_SET);

    assertThat(STRING.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(VOID.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(STRING_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(VOID.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(FILE.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(VOID.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(VOID.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(VOID.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(VOID.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(VOID.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(VOID.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(VOID.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(VOID.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void testJavaParamTypetoType() {
    assertThat(javaParamTypetoType(type(String.class))).isEqualTo(STRING);
    assertThat(javaParamTypetoType(type(StringSet.class))).isEqualTo(STRING_SET);
    assertThat(javaParamTypetoType(type(File.class))).isEqualTo(FILE);
    assertThat(javaParamTypetoType(type(FileSet.class))).isEqualTo(FILE_SET);
  }

  @Test
  public void voidIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(Void.class))).isNull();
  }

  @Test
  public void emptySetIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(EmptySet.class))).isNull();
  }

  @Test
  public void testJavaResultTypetoType() {
    assertThat(javaResultTypetoType(type(String.class))).isEqualTo(STRING);
    assertThat(javaResultTypetoType(type(StringSet.class))).isEqualTo(STRING_SET);
    assertThat(javaResultTypetoType(type(File.class))).isEqualTo(FILE);
    assertThat(javaResultTypetoType(type(FileSet.class))).isEqualTo(FILE_SET);
    assertThat(javaResultTypetoType(type(Void.TYPE))).isEqualTo(VOID);
  }

  @Test
  public void javaResultToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(JAVA_RESULT_TO_SMOOTH.values()).containsOnly(RESULT_TYPES.toArray(array));
  }

  @Test
  public void javaParamToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(JAVA_PARAM_TO_SMOOTH.values()).containsOnly(PARAM_TYPES.toArray(array));
  }

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
