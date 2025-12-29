package com.tennis.repository;

import com.tennis.database.DatabaseConnection;
import com.tennis.domain.User;
import com.tennis.mapper.UserMapper;

import java.sql.Connection;
import java.util.List;

public class UserRepository {
    private UserMapper mapper = new UserMapper();

    public User findById(Long id, Connection connection){
        try{
            return mapper.findUserById(id, DatabaseConnection.getConnection());
        } catch (Exception e){
            throw new RuntimeException("Error fetching user.", e);
        }
    }

    public User findByEmail(String email, Connection connection){
        try{
            return mapper.findUserbyEmail(email,DatabaseConnection.getConnection());
        } catch (Exception e){
            throw new RuntimeException("Error fetching user", e);
        }
    }

    public List<User> findAll(Connection connection){
        try{
            return mapper.findAllUsers(DatabaseConnection.getConnection());
        } catch (Exception e){
            throw new RuntimeException("Error fetching users list.", e);
        }
    }

    public void save(User user, Connection connection){
        try{
            if(user.getId() == null){
                mapper.insert(user,DatabaseConnection.getConnection());
            } else {
                mapper.update(user,DatabaseConnection.getConnection());
            }
        } catch (Exception e){
            throw new RuntimeException("Error saving user.", e);
        }
    }

    public void delete(User user, Connection connection){
        try {
            if (user != null){
                mapper.delete(user, DatabaseConnection.getConnection());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user.",e);
        }
    }
}
