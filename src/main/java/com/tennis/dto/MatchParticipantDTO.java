package com.tennis.dto;

public class MatchParticipantDTO {
    private Long id;
    private String name;        // "Jan Kowalski"
    private boolean isWinner;
    private String resultText;

    public MatchParticipantDTO(Long id, String name, boolean isWinner, String resultText) {
        this.id = id;
        this.name = name;
        this.isWinner = isWinner;
        this.resultText = resultText;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public String getResultText() {
        return resultText;
    }
}
