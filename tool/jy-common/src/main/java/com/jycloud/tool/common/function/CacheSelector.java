package com.jycloud.tool.common.function;

@FunctionalInterface
public interface CacheSelector<T> {
    T select() throws Exception;
}
