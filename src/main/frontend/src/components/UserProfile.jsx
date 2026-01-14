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
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    const isOwnProfile = currentUser && currentUser.id === parseInt(id);
    const isAdmin = currentUser && currentUser.userType === 'ADMIN';
    const canSeePrivateData = isOwnProfile || isAdmin;

    useEffect(() => {
        setLoading(true);
        const fetchData = async () => {
            try {
                const statsRes = await axios.get(`/api/users/${id}/stats`);
                setStats(statsRes.data.data);

                if (canSeePrivateData) {
                    const resRes = await axios.get(`/api/reservations/user/${id}`);
                    setReservations(resRes.data.data);
                }
            } catch (err) {
                setInfo({message: "Error loading profile", isSuccess: false})
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id, canSeePrivateData]);

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

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
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
                                    <span
                                        key={id}
                                        className="badge cursor-pointer"
                                        onClick={() => navigate(`/tournaments/${id}`)}
                                    >
                                    {name}
                                    </span>
                                ))
                            ) : (
                                <small>No titles yet</small>
                            )}
                        </div>
                    </div>

                    <h4>Recent Match History</h4>
                    <div className="vertical-scroll-list">
                        {stats.matches.map(match => (
                            <div
                                key={match.id}
                                className={`match-card-mini ${match.winOrLoss.toLowerCase()} cursor-pointer`}
                                onClick={() => navigate(`/match/${match.id}`)}
                            >
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

                    {canSeePrivateData ? (
                        <div className="vertical-scroll-list">
                            {reservations.length > 0 ? reservations.map(res => (
                                <div key={res.reservationId}
                                     className="reservation-card-mini"
                                     onClick={() => navigate(`/reservations/${res.reservationId}`)}>
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