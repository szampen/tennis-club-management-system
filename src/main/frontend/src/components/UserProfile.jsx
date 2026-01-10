import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/UserProfile.css';

const UserProfile = ({ currentUser }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [stats, setStats] = useState(null);
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);

    const isOwnProfile = currentUser && currentUser.id === parseInt(id);

    useEffect(() => {
        setLoading(true);
        const fetchData = async () => {
            try {
                const statsRes = await axios.get(`/api/users/${id}/stats`);
                setStats(statsRes.data.data);

                if (isOwnProfile) {
                    const resRes = await axios.get(`/api/reservations/user/${id}`);
                    setReservations(resRes.data.data);
                }
            } catch (err) {
                console.error("Error loading profile:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id, isOwnProfile]);

    if (loading) return <div className="loading">Loading profile...</div>;
    if (!stats) return <div className="error">Profile not found.</div>;

    return (
        <div className="profile-wrapper">
            <button className="btn-back" onClick={() => navigate(-1)}>← Back</button>
            {/* Action bar on top of the boxes */}
            {isOwnProfile && (
                <div className="profile-actions">
                    <button className="btn-edit-top" onClick={() => navigate('/settings')}>
                         Edit My Profile
                    </button>
                </div>
            )}

            <div className="profile-grid">
                {/* Left side : statistics and matches */}
                <div className="glass-card vertical-card">
                    <div className="card-header">
                        <h3>Performance Stats</h3>
                    </div>

                    <div className="stats-container-horizontal">
                        {/* Matches */}
                        <div className="stats-summary-box">
                            <div className="stat-circle">
                                <strong>{(stats.match_percentage * 100).toFixed(0)}%</strong>
                                <span>Match Win</span>
                            </div>
                            <div className="mini-stats">
                                <p>Wins: <strong>{stats.wins}</strong></p>
                                <p>Losses: <strong>{stats.losses}</strong></p>
                            </div>
                        </div>

                        {/* Sets */}
                        <div className="stats-summary-box secondary">
                            <div className="stat-circle purple">
                                <strong>{(stats.set_percentage * 100).toFixed(0)}%</strong>
                                <span>Sets Win</span>
                            </div>
                            <div className="mini-stats">
                                <p>S. Win: <strong>{stats.setWins}</strong></p>
                                <p>S. Loss: <strong>{stats.setLosses}</strong></p>
                            </div>
                        </div>
                    </div>

                    <div className="tournaments-section">
                        <h4>Titles Won</h4>
                        <div className="tourney-badges">
                            {stats.tournamentsWon && Object.keys(stats.tournamentsWon).length > 0 ? (
                                Object.entries(stats.tournamentsWon).map(([id, name]) => (
                                    <span key={id} className="badge">{name}</span>
                                ))
                            ) : (
                                <small>No titles yet</small>
                            )}
                        </div>
                    </div>

                    <h4>Recent Match History</h4>
                    <div className="vertical-scroll-list">
                        {stats.matches.map(match => (
                            <div key={match.id} className={`match-card-mini ${match.winOrLoss.toLowerCase()}`}>
                                <div className="match-info">
                                    <strong>{match.winOrLoss}</strong>
                                    <span>vs {match.opponentName}</span>
                                    <small>{match.tournamentName}</small>
                                </div>
                                <div className="match-score">{match.sets.join(' | ')}</div>
                            </div>
                        ))}
                    </div>

                </div>

                {/* Right side : reservations */}
                <div className="glass-card vertical-card">
                    <div className="card-header">
                        <h3> {isOwnProfile ? "My Reservations" : "Member Activity"}</h3>
                    </div>

                    {isOwnProfile ? (
                        <div className="vertical-scroll-list">
                            {reservations.length > 0 ? reservations.map(res => (
                                <div key={res.reservationId}
                                     className="reservation-card-mini"
                                     onClick={() => navigate(`/reservation/${res.reservationId}`)}>
                                    <div className="res-date">
                                        <strong>{res.startTime.split('T')[0]}</strong>
                                        <span>{res.startTime.split('T')[1].substring(0,5)}</span>
                                    </div>
                                    <span className={`status-tag ${res.status.toLowerCase()}`}>{res.status}</span>
                                </div>
                            )) : <p className="empty-msg">No reservations.</p>}
                        </div>
                    ) : (
                        <div className="guest-view">
                            <p>Reservation details are private.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default UserProfile;