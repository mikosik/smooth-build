package org.smoothbuild.lang.type;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.type.Side;
import org.smoothbuild.util.type.Sides;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeSF {
  private static final AnyTS ANY = new AnyTS();
  private static final BlobTS BLOB = new BlobTS();
  private static final BoolTS BOOL = new BoolTS();
  private static final IntTS INT = new IntTS();
  private static final NothingTS NOTHING = new NothingTS();
  private static final StringTS STRING = new StringTS();

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

  public Sides<TypeS> oneSideBound(Side side, TypeS type) {
    return switch (side) {
      case LOWER-> new Sides<>(type, any());
      case UPPER -> new Sides<>(nothing(), type);
    };
  }
}
