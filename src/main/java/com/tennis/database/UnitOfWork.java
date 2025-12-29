package com.tennis.database;

import com.tennis.mapper.DataMapper;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
    Management of transactions, UoW automatically follows changes in objects and saves them at the end of transaction
    Guarantees atomicity - everything goes through or nothing
 */
public class UnitOfWork {
    private Connection connection; // database connection

    // sets of states - INSERT/UPDATE/DELETE
    private Set<Object> newObjects = new HashSet<>();
    private Set<Object> dirtyObjects = new HashSet<>();
    private Set<Object> deletedObjects = new HashSet<>();

    private Map<Class<?>, DataMapper<?>> mappers = new HashMap<>();

    public UnitOfWork() throws Exception{
        this.connection = DatabaseConnection.getConnection();
        this.connection.setAutoCommit(false); // disabling autocommit allows UoW to work properly
    }

    public Connection getConnection(){
        return connection;
    }

    public <T> void registerMapper(Class<T> class_, DataMapper<T> mapper){
        mappers.put(class_,mapper);
    }

    // INSERT
    public void registerNew(Object object){
        if(object == null) throw new IllegalArgumentException("Cannot register null object");
        if(deletedObjects.contains(object)) throw new IllegalArgumentException("Object is marked for a deletion");

        if(!newObjects.contains(object) && !dirtyObjects.contains(object)){
            newObjects.add(object);
        }
    }

    // UPDATE
    public void registerDirty(Object object){
        if(object == null) throw new IllegalArgumentException("Cannot register null object");
        if(deletedObjects.contains(object)) throw new IllegalArgumentException("Object is marked for a deletion");

        if(!newObjects.contains(object)){
            dirtyObjects.add(object);
        }
    }

    // DELETE
    public void registerDeleted(Object object){
        if(object == null) throw new IllegalArgumentException("Cannot register null object");

        if(newObjects.remove(object)){
            return;
        }

        dirtyObjects.remove(object);
        deletedObjects.add(object);
    }

    public void commit() throws Exception{
        try{
            for (Object obj : newObjects){
                insertObject(obj);
            }
            for(Object obj : dirtyObjects){
                updateObject(obj);
            }
            for(Object obj : deletedObjects){
                deleteObject(obj);
            }

            connection.commit();

            newObjects.clear();
            dirtyObjects.clear();
            deletedObjects.clear();
        } catch (Exception e){
            rollback();
            throw new Exception("Unit of Work commit failed: " + e.getMessage(), e);
        } finally{
            close();
        }
    }

    public void rollback(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.rollback();
            }
        } catch (Exception e){
            throw new RuntimeException("Error during rollback: " + e.getMessage());
        } finally {
            newObjects.clear();
            dirtyObjects.clear();
            deletedObjects.clear();
            close();
        }
    }

    private void close(){
        try{
            if(connection != null && !connection.isClosed()){
                DatabaseConnection.returnConnection(connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error closing connection " + e.getMessage());
        }
    }

    private void insertObject(Object obj) throws Exception{
        @SuppressWarnings("unchecked")
        DataMapper<Object> mapper = (DataMapper<Object>) mappers.get(obj.getClass());
        if(mapper == null) throw new IllegalArgumentException("No mapper registered for: " + obj.getClass());

        Long id = mapper.insert(obj,connection);
    }

    private void updateObject(Object obj) throws Exception{
        @SuppressWarnings("unchecked")
        DataMapper<Object> mapper = (DataMapper<Object>) mappers.get(obj.getClass());
        if(mapper == null) throw new IllegalArgumentException("No mapper registered for: " + obj.getClass());

        mapper.update(obj, connection);
    }

    private void deleteObject(Object obj) throws Exception{
        @SuppressWarnings("unchecked")
        DataMapper<Object> mapper = (DataMapper<Object>) mappers.get(obj.getClass());
        if(mapper == null) throw new IllegalArgumentException("No mapper registered for: " + obj.getClass());

        mapper.delete(obj, connection);
    }
}
