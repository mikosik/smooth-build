package org.smoothbuild.type.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.hash.Hash;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class ImmutableStringSet implements StringSet {
  private final ImmutableList<StringValue> elements;

  public ImmutableStringSet(Iterable<String> elements) {
    Builder<StringValue> builder = ImmutableList.builder();
    for (String string : elements) {
      builder.add(new FakeString(string));
    }
    this.elements = builder.build();
  }

  @Override
  public Iterator<StringValue> iterator() {
    return elements.iterator();
  }

  // TODO remove FakeString once StringValue is used by StringTask
  public static class FakeString implements StringValue {
    private final String value;
    private final HashCode hash;

    public FakeString(String value) {
      this.value = checkNotNull(value);
      this.hash = Hash.string(value);
    }

    @Override
    public HashCode hash() {
      return hash;
    }

    @Override
    public String value() {
      return value;
    }
  }
}
