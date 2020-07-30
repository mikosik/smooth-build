package org.smoothbuild.db.record.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.db.RecordDb;

import com.google.common.collect.Lists;

public class TupleSpecTest extends AbstractRecordSpecTestCase {
  @Override
  protected Spec getSpec(RecordDb recordDb) {
    return recordDb.tupleSpec(List.of(recordDb.stringSpec(), recordDb.stringSpec()));
  }

  @Test
  public void tuple_spec_without_elements_can_be_created() {
    tupleSpec(list());
  }

  @Test
  public void tuple_spec_with_different_element_order_has_different_hash() {
    List<Spec> elements = List.of(stringSpec(), blobSpec());
    TupleSpec spec = tupleSpec(elements);
    TupleSpec spec2 = tupleSpec(Lists.reverse(elements));
    assertThat(spec.hash())
        .isNotEqualTo(spec2.hash());
  }

  @Test
  public void creating_same_tuple_twice_is_possible() {
    tupleSpec(List.of(stringSpec()));
    tupleSpec(List.of(stringSpec()));
  }
}
