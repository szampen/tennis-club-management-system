package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.domain.Match;
import com.tennis.domain.Player;
import com.tennis.domain.User;
import com.tennis.dto.*;
import com.tennis.repository.MatchRepository;
import com.tennis.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public UserService(UserRepository userRepo, MatchRepository matchRepo){
        this.userRepository = userRepo;
        this.matchRepository = matchRepo;
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public ApiResponse login(LoginRequest request){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findByEmail(request.getEmail(), conn);

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

            User existing = userRepository.findByEmail(request.getEmail(), conn);
            if(existing != null){
                return new ApiResponse(false, "Email is already used.");
            }

            if(request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()){
                return new ApiResponse(false, "Invalid email format. Use: xxx@domain.xx");
            }

            Player player = new Player();
            player.setEmail(request.getEmail());
            player.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
            player.setFirstName(request.getFirstName());
            player.setLastName(request.getLastName());
            player.setPhoneNumber(request.getPhoneNumber());

            userRepository.save(player, conn);

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

            User user = userRepository.findById(id, conn);

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

            List<User> users = userRepository.findAll(conn);

            List<UserDTO> usersDTOS = users.stream().map(DTOMapper::toUserDTO).collect(Collectors.toList());

            return new ApiResponse(true, "OK", usersDTOS);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    //TODO
    public ApiResponse getTournamentMatchesHistory(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();
            List<Match> matches = matchRepository.findByPlayer(userId, conn);

            //List<MatchListDTO> matchListDTO = matches.stream().map(DTOMapper::toMatchList).collect(Collectors.toList());

           // return new ApiResponse(true, "OK", matchListDTO);
            return new ApiResponse(true, "OK");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    //TODO: tournaments Won
    public ApiResponse getTournamentStatistics(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            int wins = matchRepository.countWins(userId,conn);
            int losses = matchRepository.countLosses(userId,conn);
            int setWin = matchRepository.countSetWon(userId,conn);
            int setLoss = matchRepository.countSetLost(userId,conn);
            double match_percentage = (double) wins /losses;
            double set_percentage = (double) setWin /setLoss;

            //statsDTO - setters

            //return new ApiResponse(true, "OK", statsDTO);
            return new ApiResponse(true, "OK");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    //TODO: public ApiResponse update() - create DTO, when frontend design is known
    //TODO: public ApiResponse delete() - soft-delete
}
