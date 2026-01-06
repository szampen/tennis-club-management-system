package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.domain.Player;
import com.tennis.domain.User;
import com.tennis.dto.*;
import com.tennis.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repo){
        this.repository = repo;
    }

    public ApiResponse login(LoginRequest request){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = repository.findByEmail(request.getEmail(), conn);

            if(user == null){
                return new ApiResponse(false, "Incorrect email.");
            }

            if(!BCrypt.checkpw(request.getPassword(),user.getPassword())){
                return new ApiResponse(false, "Incorrect password.");
            }

            UserDTO userDTO = DTOMapper.toUserDTO(user);

            return new ApiResponse(true, "You are logged in.", userDTO);

        } catch (Exception e){
            return new ApiResponse(false, "Error logging in: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse register(RegisterRequest request){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User existing = repository.findByEmail(request.getEmail(), conn);
            if(existing != null){
                return new ApiResponse(false, "Email is already used.");
            }

            Player player = new Player();
            player.setEmail(request.getEmail());
            player.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
            player.setFirstName(request.getFirstName());
            player.setLastName(request.getLastName());
            player.setPhoneNumber(request.getPhoneNumber());

            repository.save(player, conn);

            UserDTO userDTO = DTOMapper.toUserDTO(player);
            return new ApiResponse(true, "Registration was successful.", userDTO);

        } catch (Exception e) {
            return new ApiResponse(false, "Registration error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getUser(Long id){
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            User user = repository.findById(id, conn);

            if(user == null) return new ApiResponse(false, "User not found.");

            UserDTO userDTO = DTOMapper.toUserDTO(user);

            return new ApiResponse(true, "OK", userDTO);
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getAllUsers(){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<User> users = repository.findAll(conn);

            List<UserDTO> usersDTOS = users.stream().map(DTOMapper::toUserDTO).collect(Collectors.toList());

            return new ApiResponse(true, "OK", usersDTOS);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    //TODO: public ApiResponse update() - create DTO, when frontend design is known
    //TODO: public ApiResponse delete()
}
