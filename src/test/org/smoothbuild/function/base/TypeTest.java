package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;

import com.google.inject.TypeLiteral;

public class TypeTest {

  @Test
  public void javaType() {
    assertThat((Object) STRING.javaType()).isEqualTo(type(String.class));
    assertThat((Object) STRING_SET.javaType()).isEqualTo(type(StringSet.class));
    assertThat((Object) FILE.javaType()).isEqualTo(type(File.class));
    assertThat((Object) FILE_SET.javaType()).isEqualTo(type(FileSet.class));
    assertThat((Object) Type.VOID.javaType()).isEqualTo(type(Void.TYPE));
  }

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_SET)).isFalse();

    assertThat(STRING_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(STRING_SET)).isTrue();
    assertThat(STRING_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(FILE_SET)).isFalse();

    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_SET)).isFalse();

    assertThat(FILE_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE_SET)).isTrue();
  }

  @Test
  public void equalsAndHashCodeWorkaround() throws Exception {
    assertThat(STRING).isEqualTo(STRING);
    assertThat(STRING).isNotEqualTo(FILE);
    assertThat(STRING).isNotEqualTo(FILE_SET);

    assertThat(FILE).isNotEqualTo(STRING);
    assertThat(FILE).isEqualTo(FILE);
    assertThat(FILE).isNotEqualTo(FILE_SET);

    assertThat(FILE_SET).isNotEqualTo(STRING);
    assertThat(FILE_SET).isNotEqualTo(FILE);
    assertThat(FILE_SET).isEqualTo(FILE_SET);

    assertThat(STRING.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE_SET.hashCode());

    assertThat(STRING_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE_SET.hashCode());

    assertThat(FILE.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(FILE_SET.hashCode());

    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(FILE.hashCode());
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
