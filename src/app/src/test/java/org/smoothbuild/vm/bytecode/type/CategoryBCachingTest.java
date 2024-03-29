package org.smoothbuild.vm.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

public class CategoryBCachingTest extends TestContext {
  @ParameterizedTest
  @MethodSource("factories")
  public void created_type_is_cached(Function<CategoryDb, CategoryB> factory) {
    assertThat(factory.apply(categoryDb()))
        .isSameInstanceAs(factory.apply(categoryDb()));
  }

  @ParameterizedTest
  @MethodSource("factories")
  public void read_type_is_cached(Function<CategoryDb, CategoryB> factory) {
    var hash = factory.apply(categoryDb()).hash();
    var catDb = categoryDbOther();
    assertThat(catDb.get(hash))
        .isSameInstanceAs(catDb.get(hash));
  }

  private static List<Function<CategoryDb, CategoryB>> factories() {
    return list(
        CategoryDb::blob,
        CategoryDb::bool,
        CategoryBCachingTest::funcT,
        CategoryDb::int_,
        CategoryDb::string,
        CategoryBCachingTest::tupleT,

        catDb -> catDb.call(catDb.int_()),
        catDb -> catDb.combine(catDb.tuple()),
        catDb -> catDb.combine(catDb.tuple(catDb.int_())),
        catDb -> catDb.closure(catDb.funcT(list(), catDb.int_())),
        catDb -> catDb.closurize(catDb.funcT(list(), catDb.int_())),
        catDb -> catDb.exprFunc(catDb.funcT(list(), catDb.int_())),
        catDb -> catDb.ifFunc(catDb.int_()),
        catDb -> catDb.mapFunc(catDb.int_(), catDb.string()),
        catDb -> catDb.order(catDb.array(catDb.int_())),
        catDb -> catDb.pick(catDb.int_()),
        catDb -> catDb.reference(catDb.int_()),
        catDb -> catDb.select(catDb.int_()),

        catDb -> catDb.array(catDb.blob()),
        catDb -> catDb.array(catDb.bool()),
        catDb -> catDb.array(catDb.int_()),
        catDb -> catDb.array(catDb.string()),
        catDb -> catDb.array(tupleT(catDb)),
        catDb -> catDb.array(funcT(catDb)),

        catDb -> catDb.array(catDb.array(catDb.blob())),
        catDb -> catDb.array(catDb.array(catDb.bool())),
        catDb -> catDb.array(catDb.array(catDb.int_())),
        catDb -> catDb.array(catDb.array(catDb.string())),
        catDb -> catDb.array(catDb.array(tupleT(catDb))),
        catDb -> catDb.array(catDb.array(funcT(catDb)))
    );
  }

  private static TupleTB tupleT(CategoryDb categoryDb) {
    return categoryDb.tuple(categoryDb.string(), categoryDb.string());
  }

  private static FuncTB funcT(CategoryDb categoryDb) {
    return categoryDb.funcT(list(categoryDb.bool(), categoryDb.blob()), categoryDb.string());
  }
}
