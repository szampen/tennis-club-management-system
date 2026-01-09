package com.tennis.database;

import com.tennis.domain.*;
import com.tennis.mapper.*;

public class UnitOfWorkFactory {
    public static UnitOfWork create() throws Exception{
        UnitOfWork uow = new UnitOfWork();

        uow.registerMapper(User.class, new UserMapper());
        uow.registerMapper(Court.class, new CourtMapper());
        uow.registerMapper(Reservation.class, new ReservationMapper());
        uow.registerMapper(Payment.class, new PaymentMapper());
        uow.registerMapper(Tournament.class, new TournamentMapper());
        uow.registerMapper(Match.class,new MatchMapper());

        return uow;
    }
}
