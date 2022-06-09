package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.PolyFuncTS.polyFuncTS;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeSF {
  public static final AnyTS ANY = new AnyTS();
  public static final BlobTS BLOB = new BlobTS();
  public static final BoolTS BOOL = new BoolTS();
  public static final IntTS INT = new IntTS();
  public static final NothingTS NOTHING = new NothingTS();
  public static final StringTS STRING = new StringTS();

  @Inject
  public TypeSF() {
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableList<TypeS> inferableBaseTs() {
    return ImmutableList.<TypeS>builder()
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

  public ArrayTS array(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public BlobTS blob() {
    return BLOB;
  }

  public BoolTS bool() {
    return BOOL;
  }

  public PolyFuncTS polyFunc(TypeS resT, List<? extends TypeS> paramTs) {
    return polyFuncTS(func(resT, paramTs));
  }

  public FuncTS func(TypeS resT, List<? extends TypeS> paramTs) {
    return new FuncTS(resT, ImmutableList.copyOf(paramTs));
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

  public TypeS edge(Side side) {
    return switch (side) {
      case LOWER -> nothing();
      case UPPER -> any();
    };
  }

  public Bounds<TypeS> oneSideBound(Side side, TypeS type) {
    return switch (side) {
      case LOWER-> new Bounds<>(type, any());
      case UPPER -> new Bounds<>(nothing(), type);
    };
  }
}
