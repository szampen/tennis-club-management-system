import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/Tournament.css';

const TournamentList = ({ user }) => {
    const [tournaments, setTournaments] = useState([]); // TournamentListDTO
    const [filter, setFilter] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState({ name: '', tournamentRank: 'TIER_3', entryFee: 0 });
    const navigate = useNavigate();

    const isAdmin = user && user.userType === 'ADMIN';

    if (!user) {
        return (
            <div className="tournament-page-container">
                <div className="match-details-wrapper" style={{textAlign: 'center', marginTop: '100px'}}>
                    <div className="match-card-main">
                        <h2 className="text-2xl font-bold color-purple mb-4">Access limited</h2>
                        <p className="mb-6 text-gray-600">Log in, to see tournament module.</p>
                        <button className="btn-book" onClick={() => navigate('/login')}>
                            GO TO LOG IN
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    const fetchTournaments = () => {
        axios.get(`/api/tournaments${filter ? `?status=${filter}` : ''}`).then(res => setTournaments(res.data.data));
    };

    useEffect(() => { fetchTournaments(); }, [filter]);

    const handleCreate = async () => {
        const res = await axios.post('/api/tournaments', form);
        if (res.data.success) {
            // Transfer to SetupMatches
            navigate(`/tournaments/setup/${res.data.data.tournamentId}`, { state: { draft: res.data.data } });
        }
    };

    return (
        <div className="tournament-page-container">
            <aside className="court-sidebar">
                {isAdmin && (
                    <button className="btn-create-court" onClick={() => setShowModal(true)}>
                        + CREATE TOURNAMENT
                    </button>
                )}
                <div className="filter-section">
                    <h3>Filters</h3>
                    <label>Status</label>
                    <select className="sort-select" onChange={e => setFilter(e.target.value)}>
                        <option value="">All Statuses</option>
                        <option value="REGISTRATION_OPEN">Registration Open</option>
                        <option value="REGISTRATION_CLOSED">Registration Closed</option>
                        <option value="CANCELLED">Cancelled</option>
                        <option value="ONGOING">Ongoing</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>
            </aside>

            <main className="tournament-main-content">
                <div className="tournament-scroll-list">
                    {tournaments.map((t, idx) => (
                        <div key={idx} onClick={() => navigate(`/tournaments/${t.id}`)} className="tournament-card-wimbledon">
                            <div>
                                <h3 className="text-2xl font-bold text-green-800">{t.tournamentName}</h3>
                                <p className="text-gray-500">{t.tournamentRank} | {t.startDate}</p>
                            </div>
                            <span className={`status-badge status-${t.status}`}>{t.status}</span>
                        </div>
                    ))}
                </div>
            </main>

            {isAdmin && showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2 className="text-3xl font-bold color-purple">Create Tournament</h2>

                        <label>TOURNAMENT NAME</label>
                        <input
                            className="sort-select"
                            placeholder="e.g. Summer Open 2026"
                            value={form.name}
                            onChange={e => setForm({...form, name: e.target.value})}
                        />

                        <label>RANK & SIZE</label>
                        <select
                            className="sort-select"
                            value={form.tournamentRank}
                            onChange={e => setForm({...form, tournamentRank: e.target.value})}
                        >
                            <option value="TIER_3">TIER 3 (4 players)</option>
                            <option value="TIER_2">TIER 2 (8 players)</option>
                            <option value="TIER_1">TIER 1 (16 players)</option>
                        </select>

                        <div className="flex flex-col gap-3 mt-4">
                            <button onClick={handleCreate} className="btn-book w-full py-3">
                                GENERATE BRACKET & SETUP
                            </button>
                            <button onClick={() => setShowModal(false)} className="btn-back w-full" style={{marginBottom: 0}}>
                                Go Back
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TournamentList