package com.myshop.api.base;

import com.myshop.common.pagination.Pagination;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CRUDBaseServiceImpl<E, R, T, ID> implements CRUDBaseService<E, R, T, ID>, com.myshop.api.base.BaseEntityMapping<E, R, T> {
    private final Class<R> requestClazz;

    private final Class<T> responseClazz;

    private final Class<E> entityClazz;

    private final JpaSpecificationExecutor<E> repository;

    protected static ModelMapper MODEL_MAPPER = null;

    public CRUDBaseServiceImpl(
            Class<E> entityClazz, Class<R> requestClazz, Class<T> responseClazz, JpaSpecificationExecutor<E> repository) {
        this.entityClazz = entityClazz;
        this.requestClazz = requestClazz;
        this.responseClazz = responseClazz;
        this.repository = repository;
        MODEL_MAPPER = new ModelMapper();
        MODEL_MAPPER.getConfiguration().setAmbiguityIgnored(true);
        MODEL_MAPPER.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        MODEL_MAPPER.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public void mapRequestToEntity(R request, E entity) {
        MODEL_MAPPER.map(request, entity);
    }

    @Override
    public T convertToResponse(E entity) {
        return MODEL_MAPPER.map(entity, responseClazz);
    }

    @Override
    public E convertToEntity(R request) {
        return MODEL_MAPPER.map(request, entityClazz);
    }

    @Override
    public R convertToRequest(E entity) {
        return MODEL_MAPPER.map(entity, requestClazz);
    }

    public JpaSpecificationExecutor<E> getRepository() {
        return repository;
    }

    @Transactional(readOnly = true)
    @Override
    public Pagination<T> getPaging(Pageable pageable, Specification<E> specification) {
        Page<E> results = getRepository().findAll(specification, pageable);

        List<T> responseEntities = results.stream()
                .map(this::convertToResponse).collect(Collectors.toList());

        return Pagination.create(
                responseEntities, pageable.getPageNumber() + 1, pageable.getPageSize(), results.getTotalElements());
    }
}
