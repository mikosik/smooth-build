package org.smoothbuild.db.hash;

import static com.google.common.collect.Lists.newArrayList;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.testing.plugin.FakeHashed;

import com.google.common.collect.Lists;

public class HashedSorterTest {
  FakeHashed hashed1;
  FakeHashed hashed2;
  FakeHashed hashed3;
  FakeHashed hashed4;

  @Test
  public void hashed_objects_are_sorted_by_their_hashes() {
    given(hashed1 = new FakeHashed(new byte[] { 0, 0 }));
    given(hashed2 = new FakeHashed(new byte[] { 0, 1 }));
    given(hashed3 = new FakeHashed(new byte[] { 1, 0 }));
    given(hashed4 = new FakeHashed(new byte[] { 1, 1 }));
    when(HashedSorter.sort(newArrayList(hashed4, hashed3, hashed2, hashed1)));
    thenReturned(newArrayList(hashed1, hashed2, hashed3, hashed4));
  }

  @Test
  public void sorting_empty_list_returns_empty_list() throws Exception {
    when(HashedSorter.sort(Lists.<Value> newArrayList()));
    thenReturned(Lists.<Value> newArrayList());
  }
}
