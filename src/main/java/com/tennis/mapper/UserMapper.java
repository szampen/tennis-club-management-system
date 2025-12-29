package com.tennis.mapper;

import com.tennis.database.IdentityMap;
import com.tennis.domain.Admin;
import com.tennis.domain.Player;
import com.tennis.domain.User;
import com.tennis.domain.UserType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
    Maps domain model User into database
 */

//TODO: findUserbyId and Email - REMEMBER TO CHECK IF IDENTITYMAP NECESSARY
public class UserMapper implements DataMapper<User>{

    @Override
    public Long insert(User user, Connection connection) throws SQLException {
        String sql = "INSERT INTO users (email, password, first_name, last_name, phone_number, user_type, ranking_points) VALUES (?, ?, ?, ?, ?, ?, ?)".formatted();

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3,user.getFirstName());
        statement.setString(4,user.getLastName());
        statement.setString(5, user.getPhoneNumber());
        statement.setString(6,user.getUserType().name());

        if (user instanceof Player){
            statement.setInt(7, ((Player) user).getRankingPoints());
        } else{
            statement.setInt(7,0);
        }

        int rowsAffected = statement.executeUpdate();

        if(rowsAffected == 0) throw new SQLException("User insertion failed");

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if(generatedKeys.next()){
            Long id = generatedKeys.getLong(1);
            user.setId(id);
            return id;
        }

        throw new SQLException("Fetching user id failed.");
    }

    @Override
    public void update(User user, Connection connection) throws SQLException{
        String sql = "UPDATE users SET email = ?, password = ?, first_name = ?, last_name = ?, phone_number = ?, ranking_points = ? WHERE id = ?".formatted();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getFirstName());
        statement.setString(4, user.getLastName());
        statement.setString(5, user.getPhoneNumber());

        if(user instanceof Player){
            statement.setInt(6, ((Player) user).getRankingPoints());
        } else{
            statement.setInt(6,0);
        }

        statement.setLong(7, user.getId());

        statement.executeUpdate();
    }

    @Override
    public void delete(User user, Connection connection) throws SQLException{
        String sql = "DELETE FROM users WHERE id = ?".formatted();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1,user.getId());
        statement.executeUpdate();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException{
        String userType = rs.getString("user_type");
        User user;

        if("PLAYER".equals(userType)){
            Player player = new Player();
            player.setRankingPoints(rs.getInt("ranking_points"));
            user = player;
            user.setUserType(UserType.PLAYER);
        } else if("ADMIN".equals(userType)){
            user = new Admin();
            user.setUserType(UserType.ADMIN);
        } else{
            throw new SQLException("Unknown user type " + userType);
        }

        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhoneNumber(rs.getString("phone_number"));

        return user;
    }

    public List<User> findAllUsers(Connection connection) throws SQLException{
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users".formatted();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while(rs.next()){
            users.add(mapResultSetToUser(rs));
        }

        return users;
    }

    public User findUserById(Long id, Connection connection) throws SQLException{
        /*
        if(identityMap.contains(User.class,id)){
            return identityMap.get(User.class,id);
        }

        */

        String sql = "SELECT * FROM users WHERE id = ?".formatted();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1,id);

        ResultSet rs = statement.executeQuery();

        if(rs.next()){
            User user = mapResultSetToUser(rs);
            //identityMap.put(User.class,id,user);
            return user;
        }
        return null;
    }

    public User findUserbyEmail(String email, Connection connection) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = ?".formatted();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);

        ResultSet set = statement.executeQuery();

        if(set.next()){
            /*
            Long id = set.getLong("id");
            if(identityMap.contains(User.class,id)){
                return identityMap.get(User.class,id);
            }
            */
            //identityMap.put(User.class, id, user);
            return mapResultSetToUser(set);
        }
        return null;
    }
    //TODO: find by email/name etc. if needed
}
