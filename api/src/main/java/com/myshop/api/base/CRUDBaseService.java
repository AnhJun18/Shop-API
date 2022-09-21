package com.myshop.api.base;

import com.myshop.common.pagination.Pagination;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CRUDBaseService<E, R, T, ID> {

    /**
     * Get repository.
     *
     * @return a repository
     */
    JpaSpecificationExecutor<E> getRepository();

    /**
     * Retrieve all entity paging.
     *
     * @return a paging of entity
     */
    Pagination<T> getPaging(Pageable pageable, Specification<E> specification);
}
