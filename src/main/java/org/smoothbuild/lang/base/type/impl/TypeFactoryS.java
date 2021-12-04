package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeFactoryS implements TypeFactory<TypeS> {
  private static final AnyTS ANY = new AnyTS();
  private static final BlobTS BLOB = new BlobTS();
  private static final BoolTS BOOL = new BoolTS();
  private static final IntTS INT = new IntTS();
  private static final NothingTS NOTHING = new NothingTS();
  private static final StringTS STRING = new StringTS();

  private final Sides<TypeS> sides;

  @Inject
  public TypeFactoryS() {
    this.sides = new Sides<>(any(), nothing());
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableList<BaseTS> inferableBaseTs() {
    return ImmutableList.<BaseTS>builder()
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

  @Override
  public Bounds<TypeS> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<TypeS> oneSideBound(Side<TypeS> side, TypeS type) {
    return switch (side) {
      case Sides.Lower l -> new Bounds<>(type, any());
      case Sides.Upper u -> new Bounds<>(nothing(), type);
    };
  }

  @Override
  public Side<TypeS> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeS> lower() {
    return sides.lower();
  }

  public AnyTS any() {
    return ANY;
  }

  @Override
  public ArrayTS array(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public BlobTS blob() {
    return BLOB;
  }

  public BoolTS bool() {
    return BOOL;
  }

  @Override
  public FuncTS func(TypeS resT, ImmutableList<TypeS> paramTs) {
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

  public VarS var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return new VarS(name);
  }

  public StructTS struct(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }
}
