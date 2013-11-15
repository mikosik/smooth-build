package org.smoothbuild.io.cache.hash;

import java.util.List;

import org.smoothbuild.lang.function.value.Hashed;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class HashedSorter {
  public static List<Hashed> sort(List<? extends Hashed> hashedObjects) {
    Iterable<Element> elements = wrap(hashedObjects);
    List<Element> sortedElements = Ordering.natural().sortedCopy(elements);
    return unwrapElements(sortedElements);
  }

  private static Iterable<Element> wrap(List<? extends Hashed> hashedObjects) {
    List<Element> wrapped = Lists.newArrayList(new Element[hashedObjects.size()]);
    for (int i = 0; i < hashedObjects.size(); i++) {
      wrapped.set(i, new Element(hashedObjects.get(i)));
    }
    return wrapped;
  }

  private static List<Hashed> unwrapElements(List<Element> elements) {
    List<Hashed> unwrapped = Lists.newArrayList(new Hashed[elements.size()]);
    for (int i = 0; i < elements.size(); i++) {
      unwrapped.set(i, elements.get(i).value());
    }
    return unwrapped;
  }

  private static class Element implements Comparable<Element> {
    private final Hashed value;
    private final byte[] bytes;

    public Element(Hashed value) {
      this.value = value;
      this.bytes = value.hash().asBytes();
    }

    public Hashed value() {
      return value;
    }

    @Override
    public int compareTo(Element element) {
      checkLength(element);
      for (int i = 0; i < bytes.length; i++) {
        byte thisByte = this.bytes[i];
        byte thatByte = element.bytes[i];

        if (thisByte < thatByte) {
          return -1;
        }
        if (thisByte > thatByte) {
          return 1;
        }
      }
      return 0;
    }

    private void checkLength(Element element) {
      int thisLength = bytes.length;
      int thatLength = element.bytes.length;
      if (thisLength != thatLength) {
        throw new IllegalArgumentException(
            "Cannot compare two hash elements with different byte length: expected length = "
                + thisLength + ", got " + thatLength);
      }
    }
  }
}
