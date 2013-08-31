package org.smoothbuild.function.base;

import static java.lang.Void.TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_LIST;
import static org.smoothbuild.function.base.Type.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.function.base.Type.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.function.base.Type.PARAM_TYPES;
import static org.smoothbuild.function.base.Type.RESULT_TYPES;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.function.base.Type.javaParamTypetoType;
import static org.smoothbuild.function.base.Type.javaResultTypetoType;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;

public class TypeTest {

  @Test
  public void javaType() {
    assertThat((Object) STRING.javaType()).isEqualTo(String.class);
    assertThat((Object) FILE.javaType()).isEqualTo(File.class);
    assertThat((Object) FILE_LIST.javaType()).isEqualTo(FileList.class);
  }

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_LIST)).isFalse();

    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_LIST)).isFalse();

    assertThat(FILE_LIST.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_LIST.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_LIST.isAssignableFrom(FILE_LIST)).isTrue();
  }

  @Test
  public void equalsAndHashCodeWorkaround() throws Exception {
    assertThat(STRING).isEqualTo(STRING);
    assertThat(STRING).isNotEqualTo(FILE);
    assertThat(STRING).isNotEqualTo(FILE_LIST);

    assertThat(FILE).isNotEqualTo(STRING);
    assertThat(FILE).isEqualTo(FILE);
    assertThat(FILE).isNotEqualTo(FILE_LIST);

    assertThat(FILE_LIST).isNotEqualTo(STRING);
    assertThat(FILE_LIST).isNotEqualTo(FILE);
    assertThat(FILE_LIST).isEqualTo(FILE_LIST);

    assertThat(STRING.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(FILE_LIST.hashCode());
    assertThat(FILE_LIST.hashCode()).isNotEqualTo(STRING.hashCode());
  }

  @Test
  public void testJavaParamTypetoType() {
    assertThat(javaParamTypetoType(String.class)).isEqualTo(STRING);
    assertThat(javaParamTypetoType(File.class)).isEqualTo(FILE);
    assertThat(javaParamTypetoType(FileList.class)).isEqualTo(FILE_LIST);
  }

  @Test
  public void voidIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(Void.class)).isNull();
  }

  @Test
  public void testJavaResultTypetoType() {
    assertThat(javaResultTypetoType(String.class)).isEqualTo(STRING);
    assertThat(javaResultTypetoType(File.class)).isEqualTo(FILE);
    assertThat(javaResultTypetoType(FileList.class)).isEqualTo(FILE_LIST);
    assertThat(javaResultTypetoType(TYPE)).isEqualTo(VOID);
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
}
