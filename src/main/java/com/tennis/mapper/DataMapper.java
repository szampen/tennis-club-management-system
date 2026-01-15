package com.tennis.mapper;

import java.sql.Connection;

public interface DataMapper<T> {
    Long insert(T object, Connection connection) throws Exception;
    void update(T object, Connection connection) throws Exception;
    void delete(T object, Connection connection) throws Exception;
}
