package com.tennis.repository;

import com.tennis.database.UnitOfWork;
import com.tennis.domain.User;
import com.tennis.mapper.UserMapper;
import java.util.List;

public class UserRepository {
    private UserMapper mapper = new UserMapper();

    public User findById(Long id, UnitOfWork uow){
        try{
            return mapper.findUserById(id, uow.getConnection(), uow.getIdentityMap());
        } catch (Exception e){
            throw new RuntimeException("Error fetching user.", e);
        }
    }

    public User findByEmail(String email, UnitOfWork uow){
        try{
            return mapper.findUserbyEmail(email,uow.getConnection(), uow.getIdentityMap());
        } catch (Exception e){
            throw new RuntimeException("Error fetching user", e);
        }
    }

    public List<User> findAll(UnitOfWork uow){
        try{
            return mapper.findAllUsers(uow.getConnection());
        } catch (Exception e){
            throw new RuntimeException("Error fetching users list.", e);
        }
    }

    public Long save(User user, UnitOfWork uow){
        try{
            if(user.getId() == null){
                Long id = mapper.insert(user,uow.getConnection());
                uow.registerNew(user);
                return id;
            } else {
                mapper.update(user,uow.getConnection());
                uow.registerDirty(user);
                return user.getId();
            }
        } catch (Exception e){
            throw new RuntimeException("Error saving user.", e);
        }
    }

    public void delete(Long id, UnitOfWork uow){
        try {
            User user = mapper.findUserById(id, uow.getConnection(), uow.getIdentityMap());
            if (user != null){
                mapper.delete(id, uow.getConnection());
                uow.registerDeleted(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user.",e);
        }
    }
}
