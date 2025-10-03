package com.aopbuddy.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class BaseDao {
    private static final SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize SqlSessionFactory: " + e.getMessage(), e);
        }
    }

    public <T extends BaseMapper, R> R execute(Class<T> mapperClass, Function<T, R> mapperFunction) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            T mapper = session.getMapper(mapperClass);
            R result = mapperFunction.apply(mapper);
            session.commit();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute database operation: " + e.getMessage(), e);
        }
    }
}
