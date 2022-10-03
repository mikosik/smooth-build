package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class ItemS extends Tanal implements RefableS {
  private final Optional<PolyEvaluableS> defaultVal;

  public ItemS(TypeS type, String name, Optional<PolyEvaluableS> defaultVal, Loc loc) {
    super(type, name, loc);
    this.defaultVal = defaultVal;
  }

  @Override
  public ModPath modPath() {
    throw new UnsupportedOperationException();
  }

  public Optional<PolyEvaluableS> defaultVal() {
    return defaultVal;
  }

  private String defaultValToString() {
    return defaultVal.map(v -> " = " + v).orElse("");
  }

  public static ImmutableList<TypeS> toTypes(List<? extends ItemS> items) {
    return map(items, ItemS::type);
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValToString() + "`)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof ItemS that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultVal, that.defaultVal)
        && Objects.equals(this.loc(), that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), defaultVal, loc());
  }
}
