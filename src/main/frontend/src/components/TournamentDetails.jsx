import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { SingleEliminationBracket, Match } from '@g-loot/react-tournament-brackets';
import axios from 'axios';
import '../styles/Tournament.css';

const TournamentDetails = ({ user }) => {
    const { id } = useParams();
    const [data, setData] = useState(null);
    const [isActionLoading, setIsActionLoading] = useState(false); // UI Block
    const [info, setInfo] = useState({ message: '', isSuccess: false });
    const navigate = useNavigate();

    const loadTournament = async (retryCount = 0) => {
        try {
            const res = await axios.get(`/api/tournaments/${id}?userId=${user?.id}&t=${Date.now()}`);
            if (res.data.success) {
                // Work around if tournament hasn't loaded yet
                if (!res.data.data.name && retryCount < 3) {
                    setTimeout(() => loadTournament(retryCount + 1), 500);
                    return;
                }
                setData(res.data.data);
            }
        } catch (err) {
            setInfo({message: "Load error", isSuccess: false})
        }
    };

    useEffect(() => { loadTournament(); }, [id, user?.id]);

    const handleStatusChange = async (action) => {
        setIsActionLoading(true);
        try {
            const res = await axios.post(`/api/tournaments/${id}/${action}`);
            if (res.data.success) {
                // Waiting for DB save before refreshing
                setTimeout(() => {
                    loadTournament();
                    setIsActionLoading(false);
                }, 700);
            } else {
                setInfo({message: res.data.message,isSuccess: false})
                setIsActionLoading(false);
            }
        } catch (err) {
            setInfo({message: "Server error", isSuccess: false})
            setIsActionLoading(false);
        }
    };

    const handlePlayerAction = async (type) => {
        setIsActionLoading(true);
        const endpoint = type === 'register' ? 'register' : 'withdraw';
        try {
            const res = await axios.post(`/api/tournaments/${id}/${endpoint}?userId=${user.id}`);
            if (res.data.success) {
                setTimeout(() => {
                    loadTournament();
                    setIsActionLoading(false);
                }, 600);
            } else {
                setInfo({message: res.data.message,isSuccess: false})
                setIsActionLoading(false);
            }
        } catch (err) {
            setInfo({message: "Server error", isSuccess: false})
            setIsActionLoading(false);
        }
    };

    const CustomMatch = ({ match, ...props }) => (
        <Match
            {...props}
            match={{
                ...match,
                participants: match.participants.map(p => ({
                    ...p,
                    resultText: (
                        <div className="bracket-score-container">
                            {p.resultText?.split(' ').map((s, i) => (
                                <span key={i} className="score-block">{s}</span>
                            ))}
                        </div>
                    )
                }))
            }}
            onMatchClick={() => navigate(`/match/${match.id}`)}
        />
    );

    if (!data) return <div className="loader">Loading tournament...</div>;

    return (
        <div className={`tournament-page-container ${isActionLoading ? 'opacity-50 pointer-events-none' : ''}`}>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            {/* Sidebar with actions */}
            <aside className="court-sidebar">
                <button className="btn-back" onClick={() => navigate('/tournaments')} style={{color: 'white'}}>
                    ← BACK TO LIST
                </button>

                <div className="filter-section">
                    <h3>Status</h3>
                    <span className={`status-badge status-${data.status}`}>{data.status}</span>
                </div>

                <div className="filter-section">
                    <h3>Participation</h3>
                    <p><strong>Points required:</strong> {data?.rankingRequirement}</p>
                    {data.status === 'REGISTRATION_OPEN' && (
                        data.currentUserRegistered ? (
                            <button onClick={() => handlePlayerAction('withdraw')} className="btn-delete w-full">
                                WITHDRAW
                            </button>
                        ) : (
                            <button onClick={() => handlePlayerAction('register')} className="btn-create-court w-full">
                                REGISTER NOW
                            </button>
                        )
                    )}
                </div>

                {user.userType === 'ADMIN' && (
                    <div className="filter-section">
                        <h3>Admin Panel</h3>
                        <div className="flex flex-col gap-2">
                            <button onClick={() => handleStatusChange('open')} className="btn-apply-filters">Open Reg</button>
                            <button onClick={() => handleStatusChange('close')} className="btn-apply-filters">Close Reg</button>
                            <button onClick={() => handleStatusChange('cancel')} className="btn-apply-filters">Cancel</button>
                        </div>
                    </div>
                )}
            </aside>

            <main className="tournament-main-content">
                <div className="match-card-main">
                    <div className="card-header">
                        <h1 className="text-3xl font-bold text-green-900">{data.name}</h1>
                        {data.winner && (
                            <div className="status-badge confirmed">🏆 Winner: {data.winner.fullName}</div>
                        )}
                    </div>

                    <div className="bracket-section">
                        {data.matches && data.matches.length > 0 ? (
                            <SingleEliminationBracket
                                matches={data.matches.map(m => ({
                                    ...m,
                                    tournamentRoundText: m.title
                                }))}
                                matchComponent={CustomMatch}
                            />
                        ) : (
                            <div className="payment-box text-center">
                                <p className="text-gray-500 italic">Bracket has not been generated yet..</p>
                            </div>
                        )}
                    </div>

                    <div className="info-grid mt-10">
                        <div className="info-section">
                            <h3>Tournament Info</h3>
                            <p><strong>Rank:</strong> {data.tournamentRank}</p>
                            <p><strong>Players:</strong> {data.currentParticipants} / {data.maxParticipants}</p>
                        </div>
                        <div className="info-section">
                            <h3>Participant List</h3>
                            <div className="player-list max-h-40 overflow-y-auto">
                                {data?.participants?.map(p => (
                                    <p key={p.id} className="text-sm border-b py-1">👤 {p.fullName}</p>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default TournamentDetails;