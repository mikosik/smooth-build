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
  public static final AnyTS ANY = new AnyTS();
  public static final BlobTS BLOB = new BlobTS();
  public static final BoolTS BOOL = new BoolTS();
  public static final IntTS INT = new IntTS();
  public static final NothingTS NOTHING = new NothingTS();
  public static final StringTS STRING = new StringTS();

  @Inject
  public TypeFS() {
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableList<MonoTS> inferableBaseTs() {
    return ImmutableList.<MonoTS>builder()
        .addAll(baseTs())
        .add(any())
        .build();
  }

  /**
   * Base types that are legal in smooth language.
   */
  public ImmutableList<BaseTS> baseTs() {
    return ImmutableList.of(
        blob(),
        bool(),
        int_(),
        nothing(),
        string()
    );
  }

  public AnyTS any() {
    return ANY;
  }

  public ArrayTS array(MonoTS elemT) {
    return new ArrayTS(elemT);
  }

  public BlobTS blob() {
    return BLOB;
  }

  public BoolTS bool() {
    return BOOL;
  }

  public PolyFuncTS polyFunc(MonoTS resT, List<? extends MonoTS> paramTs) {
    return polyFuncTS(func(resT, paramTs));
  }

  public MonoFuncTS func(MonoTS resT, List<? extends MonoTS> paramTs) {
    return new MonoFuncTS(resT, ImmutableList.copyOf(paramTs));
  }

  public IntTS int_() {
    return INT;
  }

  public NothingTS nothing() {
    return NOTHING;
  }

  public StringTS string() {
    return STRING;
  }

  public StructTS struct(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public VarS var(String name) {
    return new VarS(name);
  }

  public MonoTS edge(Side side) {
    return switch (side) {
      case LOWER -> nothing();
      case UPPER -> any();
    };
  }
}
