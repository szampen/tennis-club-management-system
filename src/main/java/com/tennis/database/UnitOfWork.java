package com.tennis.database;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

/*
    Management of transactions, UoW automatically follows changes in objects and saves them at the end of transaction
    Guarantees atomicity - everything goes through or nothing
 */
public class UnitOfWork {
    private Connection connection; // database connection
    private IdentityMap identityMap;

    // sets of states - INSERT/UPDATE/DELETE
    private Set<Object> newObjects = new HashSet<>();
    private Set<Object> dirtyObjects = new HashSet<>();
    private Set<Object> deletedObjects = new HashSet<>();

    public UnitOfWork() throws Exception{
        this.connection = DatabaseConnection.getConnection();
        this.connection.setAutoCommit(false); // disabling autocommit allows UoW to work properly
        this.identityMap = new IdentityMap();
    }

    public Connection getConnection(){
        return connection;
    }

    public IdentityMap getIdentityMap(){
        return identityMap;
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

    private void rollback(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.rollback();
            }
        } catch (Exception e){
            System.err.println("Error during rollback: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Error closing connection " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: PLACEHOLDERS
    private void insertObject(Object obj) throws Exception{
        System.out.println("INSERT");
    }

    private void updateObject(Object obj) throws Exception{
        System.out.println("UPDATE");
    }

    private void deleteObject(Object obj) throws Exception{
        System.out.println("DELETION");
    }
}
