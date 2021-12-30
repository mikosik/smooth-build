package org.smoothbuild.bytecode.obj.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeObjIllegalPolymorphicTypeExcTest extends TestingContext {
  @Test
  public void array_message() {
    var type = arrayTB(varTB("A"));
    var hash = Hash.of(33);
    assertThat(new DecodeObjIllegalPolymorphicTypeExc(hash, type).getMessage())
        .isEqualTo("Cannot decode `[A]` object at " + hash + ". ARRAY cannot be polymorphic.");
  }

  @Test
  public void tuple_message() {
    var type = tupleTB(list(varTB("A")));
    var hash = Hash.of(33);
    assertThat(new DecodeObjIllegalPolymorphicTypeExc(hash, type).getMessage())
        .isEqualTo("Cannot decode `{A}` object at " + hash + ". TUPLE cannot be polymorphic.");
  }
}
