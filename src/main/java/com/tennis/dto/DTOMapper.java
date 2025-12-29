package com.tennis.dto;

import com.tennis.domain.Player;
import com.tennis.domain.User;

public class DTOMapper {
    public static UserDTO toUserDTO(User user){
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUserType(user.getUserType().name());

        if(user instanceof Player){
            dto.setRankingPoints(((Player) user).getRankingPoints());
        }

        return dto;
    }
}
