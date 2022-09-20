package com.myshop.api.base;

public interface BaseEntityMapping<E, R, T> {
    /**
     * Convert an entity to a model response.
     *
     * @param entity the given entity
     * @return a model response
     */
    T convertToResponse(E entity);

    /**
     * Convert a model request to an entity.
     *
     * @param request the given model request
     * @return an entity
     */
    E convertToEntity(R request);

    /**
     * Convert a entity to an model request.
     *
     * @param entity the given entity
     * @return an model request
     */
    R convertToRequest(E entity);

    /**
     * Update info from request to entity.
     *
     * @param request the given request info
     * @param entity the given entity
     */
    void mapRequestToEntity(R request, E entity);
}
