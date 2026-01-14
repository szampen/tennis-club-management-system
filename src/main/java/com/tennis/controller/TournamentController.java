package com.tennis.controller;

import com.tennis.domain.TournamentStatus;
import com.tennis.dto.ApiResponse;
import com.tennis.dto.CreateTournamentRequest;
import com.tennis.service.TournamentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ApiResponse getTournaments(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return tournamentService.getTournamentByStatus(TournamentStatus.valueOf(status));
        }
        return tournamentService.getAllTournaments();
    }

    @GetMapping("/{id}")
    public ApiResponse getTournament(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        return tournamentService.getTournament(id, userId);
    }

    @PostMapping
    public ApiResponse create(@RequestBody CreateTournamentRequest request) {
        return tournamentService.createTournament(request);
    }

    @PostMapping("/{id}/finalize")
    public ApiResponse finalize(@PathVariable Long id) {
        return tournamentService.finalizeTournament(id);
    }

    @PostMapping("/{id}/register")
    public ApiResponse register(@PathVariable Long id, @RequestParam Long userId) {
        return tournamentService.registerForTournament(userId, id);
    }

    @PostMapping("/{id}/withdraw")
    public ApiResponse withdraw(@PathVariable Long id, @RequestParam Long userId) {
        return tournamentService.withdrawFromTournament(userId, id);
    }

    @GetMapping("/match/{matchId}")
    public ApiResponse getMatch(@PathVariable Long matchId) {
        return tournamentService.getMatch(matchId);
    }

    @PostMapping("/match/{matchId}/score")
    public ApiResponse addScore(@PathVariable Long matchId, @RequestParam int p1, @RequestParam int p2) {
        return tournamentService.addScoreSetToMatch(matchId, p1, p2);
    }

    @PostMapping("/{id}/open")
    public ApiResponse openReg(@PathVariable Long id) {
        return tournamentService.openRegistrationForTournament(id);
    }

    @PostMapping("/{id}/close")
    public ApiResponse closeReg(@PathVariable Long id) {
        return tournamentService.closeRegistrationForTournament(id);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse cancel(@PathVariable Long id) {
        return tournamentService.cancelTournament(id);
    }
}
