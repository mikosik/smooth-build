package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.like.ItemLike;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public record Item(Type type, String name, Optional<Expression> defaultValue) implements ItemLike {
  public Item(Type type, String name, Optional<Expression> defaultValue) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.defaultValue = requireNonNull(defaultValue);
  }

  public ItemSignature signature() {
    return new ItemSignature(type, Optional.of(name), defaultValue.map(Expression::type));
  }

  @Override
  public boolean hasDefaultValue() {
    return defaultValue.isPresent();
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValueToString() + "`)";
  }

  private String defaultValueToString() {
    return defaultValue.map(v -> " = " + v).orElse("");
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<Item> items) {
    return map(items, Item::signature);
  }
}
