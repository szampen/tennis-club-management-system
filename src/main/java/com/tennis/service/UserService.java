package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.domain.Match;
import com.tennis.domain.Player;
import com.tennis.domain.Tournament;
import com.tennis.domain.User;
import com.tennis.dto.*;
import com.tennis.repository.MatchRepository;
import com.tennis.repository.TournamentRepository;
import com.tennis.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public UserService(UserRepository userRepo, MatchRepository matchRepo, TournamentRepository tournamentRepository){
        this.userRepository = userRepo;
        this.matchRepository = matchRepo;
        this.tournamentRepository = tournamentRepository;
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

    //TODO: is it needed?
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

    public ApiResponse getPlayerStatistics(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            int wins = matchRepository.countWins(userId,conn);
            int losses = matchRepository.countLosses(userId,conn);
            int setWin = matchRepository.countSetWon(userId,conn);
            int setLoss = matchRepository.countSetLost(userId,conn);
            double match_percentage;
            double set_percentage;
            if(losses == 0){
                if(wins == 0) match_percentage = 0.0;
                else match_percentage = 1.0;
            } else {
                match_percentage = (double) wins /(wins+losses);
            }
            if(setLoss == 0){
                if(setWin == 0) set_percentage = 0.0;
                else set_percentage = 1.0;
            } else {
                set_percentage = (double) setWin /(setWin+setLoss);
            }

            List<Tournament> tournamentsWon = tournamentRepository.tournamentsWonByUser(userId, conn);
            for(Tournament t : tournamentsWon){

            }

            List<Match> matches = matchRepository.findByPlayer(userId,conn);
            List<MatchListDTO> matchDtos = new ArrayList<>();
            for(Match m : matches){
                Tournament temp = tournamentRepository.findById(m.getTournamentId(),conn);
                String opponentName;
                if(m.getPlayer1Id() == userId){
                    User opponent = userRepository.findById(m.getPlayer2Id(),conn);
                    opponentName = opponent.getFirstName() + " " + opponent.getLastName();
                }
                else{
                    User opponent = userRepository.findById(m.getPlayer1Id(),conn);
                    opponentName = opponent.getFirstName() + " " + opponent.getLastName();
                }
                matchDtos.add(DTOMapper.toMatchListDTO(userId,m,temp, opponentName));
            }

            PlayerStatsDTO statsDTO = DTOMapper.toPlayerStatsDTO(wins,losses,setWin,setLoss,match_percentage,set_percentage,tournamentsWon,matchDtos);

            return new ApiResponse(true, "OK", statsDTO);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeEmail(Long userId, String email){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            if(!EMAIL_PATTERN.matcher(email).matches()){
                return new ApiResponse(false, "Invalid email format. Use: xxx@domain.xx");
            }

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            user.setEmail(email);
            userRepository.save(user,conn);

            return new ApiResponse(true, "Email changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changePassword(Long userId, String password){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            user.setPassword(BCrypt.hashpw(password,BCrypt.gensalt()));
            userRepository.save(user,conn);

            return new ApiResponse(true, "Password changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeFirstName(Long userId, String firstName){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            user.setFirstName(firstName);
            userRepository.save(user,conn);

            return new ApiResponse(true, "First name changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeLastName(Long userId, String lastName){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            user.setLastName(lastName);
            userRepository.save(user,conn);

            return new ApiResponse(true, "Last name changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changePhoneNumber(Long userId, String phoneNumber){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            user.setPhoneNumber(phoneNumber);
            userRepository.save(user,conn);

            return new ApiResponse(true, "Phone number changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse deleteUser(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User user = userRepository.findById(userId, conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            userRepository.delete(user,conn);

            return new ApiResponse(true, "User deleted.");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    //TODO: public ApiResponse delete() - soft-delete
}
