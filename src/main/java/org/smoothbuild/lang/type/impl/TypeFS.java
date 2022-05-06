package org.smoothbuild.lang.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.impl.TypeNamesS.isVarName;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.TupleT;
import org.smoothbuild.lang.type.api.VarBoundsS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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

  public BoundedS bounded(VarS var, Sides<TypeS> sides) {
    return new BoundedS(var, sides);
  }

  public VarBoundsS varBounds(ImmutableMap<VarS, BoundedS> map) {
    return new VarBoundsS(map);
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

  public FuncTS func(VarSetS tParams, TypeS resT, ImmutableList<TypeS> paramTs) {
    return new FuncTS(tParams, resT, ImmutableList.copyOf(paramTs));
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

  public TupleT tuple(ImmutableList<TypeS> items) {
    throw new UnsupportedOperationException();
  }

  public VarS var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return new VarS(name);
  }

  public VarSetS varSet(Set<TypeS> elements) {
    return new VarSetS((Set<VarS>)(Object) elements);
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
