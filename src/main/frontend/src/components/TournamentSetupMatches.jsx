import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import '../styles/Tournament.css';

const TournamentSetupMatches = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [draft, setDraft] = useState(null);
    const [loading, setLoading] = useState(true);
    const [info, setInfo] = useState({ message: '', isSuccess: false });
    const location = useLocation();

    // function fetching data about tournament draft and matches
    const fetchDraftData = async (retryCount = 0) => {
        try {
            //timestamp t prevents electron from loading data from cache (hopefully)
            const res = await axios.get(`/api/tournaments/${id}?t=${Date.now()}`);
            if (res.data.success) {
                const tournamentData = res.data.data;
                setDraft(tournamentData);

                // Checking if we came back from making reservation, we want to ensure user see actual data
                const hasUnreserved = tournamentData.matches.some(m => !m.reserved);

                if (hasUnreserved && retryCount < 5) {
                    console.log("Waiting for database actualization.");
                    setTimeout(() => fetchDraftData(retryCount + 1), 1000);
                }
            }
        } catch (err) {
            setInfo({message:"Error refreshing",isSuccess:false})
        } finally {
            if (retryCount === 0) setLoading(false);
        }
    };

    useEffect(() => {
        // If you came back from reservation, ensure quick refresh
        if (location.state?.matchId) {
            const timer = setTimeout(() => fetchDraftData(), 500);
            return () => clearTimeout(timer);
        } else {
            fetchDraftData();
        }
    }, [id, location]); // location reacts on returning from different page


    // Check if all the matches have reservations
    const allReserved = draft?.matches && draft.matches.length > 0
        ? draft.matches.every(m => m.reserved)
        : false;

    const handleFinalize = async () => {
        try {
            const res = await axios.post(`/api/tournaments/${id}/finalize`);
            if (res.data.success) {
                await new Promise(resolve => setTimeout(resolve, 300));
                navigate(`/tournaments/${id}`);
            } else {
                setInfo({message: res.data.message,isSuccess: false});
            }
        } catch (err) {
            setInfo({message: "Error while finalizing tournament.", isSuccess: false});
        }
    };

    if (loading) return <div>Loading matches configuration...</div>;

    return (
        <div className="match-details-wrapper">
            <h1 className="text-3xl font-bold mb-8 text-green-900">Setup Matches: {draft?.tournamentName}</h1>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <div className="setup-list">
                {draft?.matches.map(m => (
                    <div key={m.id} className="setup-item-card">
                        <div>
                            <span className="font-bold text-lg color-purple">Match #{m.id}</span>
                            <p className="text-sm text-gray-500">Round: {m.round}</p>
                        </div>

                        <button
                            disabled={m.reserved}
                            onClick={() => navigate('/reserve', { state: { matchId: m.id, isTournament: true, tournamentId: id } })}
                            className={m.reserved ? "btn-apply-filters" : "btn-book"}
                        >
                            {m.reserved ? '✓ RESERVED' : 'CREATE RESERVATION'}
                        </button>
                    </div>
                ))}
            </div>

            <div className="finalize-box">
                <p className="mb-6 text-gray-600">
                    {allReserved ? "All matches scheduled. Ready to start!" : "All matches must have a reservation."}
                </p>
                <button
                    onClick={handleFinalize}
                    disabled={!allReserved}
                    className={allReserved ? "btn-create-court w-full py-4 scale-105" : "btn-apply-filters w-full py-4"}
                >
                    FINALIZE TOURNAMENT
                </button>
            </div>
        </div>
    );
};

export default TournamentSetupMatches;