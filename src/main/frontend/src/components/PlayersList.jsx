import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/playerslist.css';

const PlayersList = () => {
    const [players, setPlayers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [info, setInfo] = useState({ message: '', isSuccess: false });
    const navigate = useNavigate();

    useEffect(() => {
        axios.get('/api/users/all')
            .then(res => {
                if (res.data.success) {
                    setPlayers(res.data.data);
                } else{
                    setInfo({ message: res.data.message, isSuccess: false });
                }
                setLoading(false);
            })
            .catch(err => {
                setInfo({ message: "Error fetching players.", isSuccess: false });
                setLoading(false);
            });
    }, []);

    // Filtrowanie graczy po imieniu lub nazwisku
    const filteredPlayers = players.filter(p =>
        (p.firstName + " " + p.lastName).toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading">Gathering players... 🎾</div>;

    return (
        <div className="players-container">
            <button className="btn-back" onClick={() => navigate(-1)}>← Back</button>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <div className="players-header">
                <h1>Tennis Club Members</h1>
                <input
                    type="text"
                    placeholder="Search player..."
                    className="search-bar"
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <div className="players-scroll-list">
                {filteredPlayers.length > 0 ? (
                    filteredPlayers.map(player => (
                        <div
                            key={player.id}
                            className="player-card glass-card"
                            onClick={() => navigate(`/user/${player.id}`)}
                        >
                            <div className="player-avatar">
                                {player.firstName[0]}{player.lastName[0]}
                            </div>
                            <div className="player-info">
                                <h3>{player.firstName} {player.lastName}</h3>
                            </div>
                            <div className="player-points-section">
                                <span className="player-rank">{player.rankingPoints || 0} pts</span>
                            </div>
                        </div>
                    ))
                ) : (
                    <p className="no-players">No players found.</p>
                )}
            </div>
        </div>
    );
};

export default PlayersList;