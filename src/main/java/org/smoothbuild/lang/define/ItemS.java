package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tanal;
import org.smoothbuild.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Item is a func param or a struct field.
 *
 * This class is immutable.
 */
public class ItemS extends Tanal implements RefableS {
  private final Optional<MonoObjS> body;
  private final ItemSigS sig;

  public ItemS(TypeS type, String name, Optional<MonoObjS> body, Loc loc) {
    super(type, name, loc);
    this.body = body;
    this.sig = new ItemSigS(type(), name());
  }

  public ItemSigS sig() {
    return sig;
  }

  public Optional<MonoObjS> body() {
    return body;
  }

  private String defaultValToString() {
    return body.map(v -> " = " + v).orElse("");
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
        && Objects.equals(this.body, that.body)
        && Objects.equals(this.loc(), that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body, loc());
  }
}
