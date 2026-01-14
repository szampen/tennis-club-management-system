import React, { useState, useEffect } from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import '../styles/Tournament.css';

const MatchDetails = ({ user }) => {
    const { matchId } = useParams();
    const [match, setMatch] = useState(null); // MatchDetailsDTO
    const [score, setScore] = useState({ p1: 0, p2: 0 });
    const navigate = useNavigate();

    useEffect(() => {
        axios.get(`/api/tournaments/match/${matchId}`).then(res => setMatch(res.data.data));
    }, [matchId]);

    const addSet = async () => {
        await axios.post(`/api/tournaments/match/${matchId}/score?p1=${score.p1}&p2=${score.p2}`);
        window.location.reload();
    };

    if (!match) return "Loading...";

    return (
        <div className="match-details-wrapper">
            <button className="btn-back" onClick={() => navigate(-1)}>← BACK</button>
            <div className="match-card-main">
                <div className="card-header">
                    <h2>{match.tournamentName}</h2>
                    <span className="status-badge confirmed">Match Details</span>
                </div>

                <div className="scoreboard-header">
                    <div className="text-center">
                        <p className="font-bold text-xl">{match.player1FullName || "TBD"}</p>
                        <div className="score-large">{match.finalScore.split(':')[0]}</div>
                    </div>
                    <div className="vs-divider-text">vs</div>
                    <div className="text-center">
                        <p className="font-bold text-xl">{match.player2FullName || "TBD"}</p>
                        <div className="score-large">{match.finalScore.split(':')[1]}</div>
                    </div>
                </div>

                <div className="payment-box"> {/* Background from ReservationDetails */}
                    <div className="info-grid"> {/* Grid from CourtList */}
                        <div className="info-item"><label>Sets:</label> <span>{match.sets.join(' | ')}</span></div>
                        <div className="info-item"><label>Court:</label> <span>{match.courtName} (#{match.courtNumber})</span></div>
                        <div className="info-item"><label>Scheduled:</label> <span>{match.scheduledTime}</span></div>
                    </div>
                </div>

                {user.userType === 'ADMIN' && !match.winnerId && (
                    <div className="admin-score-panel">
                        <h3 className="admin-panel-title">Add Set Result</h3>

                        <div className="score-input-grid">
                            <div className="player-score-field">
                                <label>{match.player1FullName || "Player 1"}</label>
                                <input
                                    type="number"
                                    min="0"
                                    placeholder="0"
                                    onChange={e => setScore({...score, p1: e.target.value})}
                                />
                            </div>

                            <div className="score-separator">:</div>

                            <div className="player-score-field">
                                <label>{match.player2FullName || "Player 2"}</label>
                                <input
                                    type="number"
                                    min="0"
                                    placeholder="0"
                                    onChange={e => setScore({...score, p2: e.target.value})}
                                />
                            </div>
                        </div>

                        <button onClick={addSet} className="btn-save-score">
                            SAVE SET RESULT
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MatchDetails