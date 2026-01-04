package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.domain.Player;
import com.tennis.domain.User;
import com.tennis.dto.*;
import com.tennis.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository repository = new UserRepository();

    public ApiResponse login(LoginRequest request){
        try{

            User user = repository.findByEmail(request.getEmail(), DatabaseConnection.getConnection());

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
        }
    }

    public ApiResponse register(RegisterRequest request){
        try{
            User existing = repository.findByEmail(request.getEmail(), DatabaseConnection.getConnection());
            if(existing != null){
                return new ApiResponse(false, "Email is already used.");
            }

            Player player = new Player();
            player.setEmail(request.getEmail());
            player.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
            player.setFirstName(request.getFirstName());
            player.setLastName(request.getLastName());
            player.setPhoneNumber(request.getPhoneNumber());

            repository.save(player, DatabaseConnection.getConnection());

            UserDTO userDTO = DTOMapper.toUserDTO(player);
            return new ApiResponse(true, "Registration was successful.", userDTO);

        } catch (Exception e) {
            return new ApiResponse(false, "Registration error: " + e.getMessage());
        }
    }

    public ApiResponse getUser(Long id){
        try {
            User user = repository.findById(id, DatabaseConnection.getConnection());

            if(user == null) return new ApiResponse(false, "User not found.");

            UserDTO userDTO = DTOMapper.toUserDTO(user);

            return new ApiResponse(true, "OK", userDTO);
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        }
    }

    public ApiResponse getAllUsers(){
        try{
            List<User> users = repository.findAll(DatabaseConnection.getConnection());

            List<UserDTO> usersDTOS = users.stream().map(DTOMapper::toUserDTO).collect(Collectors.toList());

            return new ApiResponse(true, "OK", usersDTOS);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        }
    }

    //TODO: public ApiResponse update() - create DTO, when frontend design is known
    //TODO: public ApiResponse delete() with UnitOfWork - deleting reservations
}
