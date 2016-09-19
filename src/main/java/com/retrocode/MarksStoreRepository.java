package com.retrocode;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;


public interface MarksStoreRepository extends PagingAndSortingRepository<MarksStore, String>, QueryDslPredicateExecutor<MarksStore> {

	@RestResource(rel = "by-location")
	Page<MarksStore> findByAddressLocationNear(@Param("location") Point location, @Param("distance") Distance distance, Pageable pageable);

//	default void customize(QuerydslBindings bindings, QStore store) {
//
//		bindings.bind(store.address.city).first((path, value) -> path.endsWith(value));
//		bindings.bind(String.class).first((StringPath path, String value) -> path.contains(value));
//	}

	@RestResource(rel = "by-province")
	Page<MarksStore> findByAddressProvince(@Param("province") String province, Pageable pageable);

    @RestResource(rel = "by-number")
    Page<MarksStore> findByNumber(@Param("number") String number, Pageable pageable);
}
