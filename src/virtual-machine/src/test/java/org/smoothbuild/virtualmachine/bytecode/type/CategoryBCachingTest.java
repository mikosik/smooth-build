package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class CategoryBCachingTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("factories")
  public void created_type_is_cached(Function1<CategoryDb, CategoryB, BytecodeException> factory)
      throws Exception {
    assertThat(factory.apply(categoryDb())).isSameInstanceAs(factory.apply(categoryDb()));
  }

  @ParameterizedTest
  @MethodSource("factories")
  public void read_type_is_cached(Function1<CategoryDb, CategoryB, BytecodeException> factory)
      throws Exception {
    var hash = factory.apply(categoryDb()).hash();
    var categoryDb = categoryDbOther();
    assertThat(categoryDb.get(hash)).isSameInstanceAs(categoryDb.get(hash));
  }

  private static java.util.List<Function1<CategoryDb, CategoryB, BytecodeException>> factories() {
    return list(
        CategoryDb::blob,
        CategoryDb::bool,
        CategoryBCachingTest::funcT,
        CategoryDb::int_,
        CategoryDb::string,
        CategoryBCachingTest::tupleT,
        categoryDb -> categoryDb.call(categoryDb.int_()),
        categoryDb -> categoryDb.combine(categoryDb.tuple()),
        categoryDb -> categoryDb.combine(categoryDb.tuple(categoryDb.int_())),
        categoryDb -> categoryDb.lambda(categoryDb.funcT(list(), categoryDb.int_())),
        categoryDb -> categoryDb.ifFunc(categoryDb.int_()),
        categoryDb -> categoryDb.mapFunc(categoryDb.int_(), categoryDb.string()),
        categoryDb -> categoryDb.order(categoryDb.array(categoryDb.int_())),
        categoryDb -> categoryDb.pick(categoryDb.int_()),
        categoryDb -> categoryDb.reference(categoryDb.int_()),
        categoryDb -> categoryDb.select(categoryDb.int_()),
        categoryDb -> categoryDb.array(categoryDb.blob()),
        categoryDb -> categoryDb.array(categoryDb.bool()),
        categoryDb -> categoryDb.array(categoryDb.int_()),
        categoryDb -> categoryDb.array(categoryDb.string()),
        categoryDb -> categoryDb.array(tupleT(categoryDb)),
        categoryDb -> categoryDb.array(funcT(categoryDb)),
        categoryDb -> categoryDb.array(categoryDb.array(categoryDb.blob())),
        categoryDb -> categoryDb.array(categoryDb.array(categoryDb.bool())),
        categoryDb -> categoryDb.array(categoryDb.array(categoryDb.int_())),
        categoryDb -> categoryDb.array(categoryDb.array(categoryDb.string())),
        categoryDb -> categoryDb.array(categoryDb.array(tupleT(categoryDb))),
        categoryDb -> categoryDb.array(categoryDb.array(funcT(categoryDb))));
  }

  private static TupleTB tupleT(CategoryDb categoryDb) throws BytecodeException {
    return categoryDb.tuple(categoryDb.string(), categoryDb.string());
  }

  private static FuncTB funcT(CategoryDb categoryDb) throws BytecodeException {
    return categoryDb.funcT(list(categoryDb.bool(), categoryDb.blob()), categoryDb.string());
  }
}
