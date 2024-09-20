package com.paranmanzang.fileservice.model.repository.custom;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileCustomRepository {
    Flux<?> findByRefId(Long refId, int type);
    Mono<?> findByPath(String path);

    Mono<?> deleteByPath(String path);
}