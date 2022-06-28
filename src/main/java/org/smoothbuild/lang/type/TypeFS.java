package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.PolyFuncTS.polyFuncTS;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeFS {
  private static final AnyTS ANY = new AnyTS();
  private static final BlobTS BLOB = new BlobTS();
  private static final BoolTS BOOL = new BoolTS();
  private static final IntTS INT = new IntTS();
  private static final NothingTS NOTHING = new NothingTS();
  private static final StringTS STRING = new StringTS();

  @Inject
  public TypeFS() {
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public static ImmutableList<MonoTS> inferableBaseTs() {
    return ImmutableList.<MonoTS>builder()
        .addAll(baseTs())
        .add(any())
        .build();
  }

  /**
   * Base types that are legal in smooth language.
   */
  public static ImmutableList<BaseTS> baseTs() {
    return ImmutableList.of(
        blob(),
        bool(),
        int_(),
        nothing(),
        string()
    );
  }

  public static AnyTS any() {
    return ANY;
  }

  public static ArrayTS array(MonoTS elemT) {
    return new ArrayTS(elemT);
  }

  public static BlobTS blob() {
    return BLOB;
  }

  public static BoolTS bool() {
    return BOOL;
  }

  public static PolyFuncTS polyFunc(MonoTS resT, List<? extends MonoTS> paramTs) {
    return polyFuncTS(func(resT, paramTs));
  }

  public static MonoFuncTS func(MonoTS resT, List<? extends MonoTS> paramTs) {
    return new MonoFuncTS(resT, ImmutableList.copyOf(paramTs));
  }

  public static IntTS int_() {
    return INT;
  }

  public static NothingTS nothing() {
    return NOTHING;
  }

  public static StringTS string() {
    return STRING;
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public static VarS var(String name) {
    return new VarS(name);
  }
}
