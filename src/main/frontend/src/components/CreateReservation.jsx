import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/createreservation.css';

const CreateReservation = ({ user }) => {
    const { courtId: urlCourtId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    //Entry parameters - from Navigation or URL
    const { matchId, isTournament , tournamentId} = location.state || { isTournament: false, matchId: null, tournamentId : null };

    const [selectedCourtId, setSelectedCourtId] = useState(urlCourtId || null);
    const [availableCourts, setAvailableCourts] = useState([]); // For tournament reservations
    const [selectedDate, setSelectedDate] = useState(getTomorrowDate());
    const [isInitialDateSet, setIsInitialDateSet] = useState(false);
    const [slots, setSlots] = useState([]);
    const [selectedSlotIndexes, setSelectedSlotIndexes] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    const payload = {
        surfaceType: null,
        hasRoof: null,
        availableForReservations: true,
        courtSort: 'COURTNUMBER',
        direction: 'ASC'
    };

    function getTomorrowDate() {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        return tomorrow.toISOString().split('T')[0];
    }

    //Fetching courts list for tournament reservation
    useEffect(() => {
        if (selectedCourtId) {
            setIsInitialDateSet(false);

            axios.get(`/api/courts/${selectedCourtId}`)
                .then(res => {
                    if (res.data.success && res.data.data.firstAvailableDate) {
                        setSelectedDate(res.data.data.firstAvailableDate);
                    }
                    setIsInitialDateSet(true);
                })
                .catch(err => {
                    setInfo({message: "Error fetching court details", isSuccess: false});
                    setIsInitialDateSet(true);
                });
        }

        if (isTournament && !urlCourtId) {
            axios.post('/api/courts/filtered', payload)
                .then(res => {
                    if (res.data.success) {
                        setAvailableCourts(res.data.data);
                    } else {
                        setError(res.data.message);
                    }
                })
                .catch(() => setError("Error fetching courts list."));
        }
    }, [selectedCourtId,isTournament, urlCourtId]);

    // Fetching slots for every date/court change
    useEffect(() => {
        if (selectedCourtId && selectedDate) {
            setSlots([]); // Clean old slots to avoid using invalid data
            fetchSlots();
        }
    }, [selectedCourtId, selectedDate]);

    const fetchSlots = async () => {
        setLoading(true);
        setError('');
        await new Promise(resolve => setTimeout(resolve, 500));
        try {
            const res = await axios.get(`/api/courts/${selectedCourtId}/availability?date=${selectedDate}`);
            if (res.data.success) {
                setSlots(res.data.data);
                setSelectedSlotIndexes([]);
            } else {
                setError(res.data.message);
            }
        } catch (err) {
            setError("Error fetching court's availability.");
        } finally {
            setLoading(false);
        }
    };

    // Slots must be next to each other
    const handleSlotClick = (index) => {
        setError('');
        if (selectedSlotIndexes.includes(index)) {
            // Unclick - clears all choices
            setSelectedSlotIndexes([]);
            return;
        }

        const newSelection = [...selectedSlotIndexes, index].sort((a, b) => a - b);

        // Checking continuity
        if (newSelection.length > 1) {
            const isConsecutive = newSelection.every((val, i) =>
                i === 0 || val === newSelection[i-1] + 1
            );
            if (!isConsecutive) {
                setError("Slots must be reserved without gaps.");
                return;
            }
        }
        setSelectedSlotIndexes(newSelection);
    };

    // Date navigation
    const changeDay = (offset) => {
        const current = new Date(selectedDate);
        current.setDate(current.getDate() + offset);
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0,0,0,0);

        if (current < tomorrow) return; // Blocks dates before tomorrow
        setSelectedDate(current.toISOString().split('T')[0]);
    };

    // Finalization of reservation
    const handleSubmit = async () => {
        if (selectedSlotIndexes.length === 0) {
            setError("Choose at least one slot.");
            return;
        }

        const firstSlot = slots[selectedSlotIndexes[0]];
        const lastSlot = slots[selectedSlotIndexes[selectedSlotIndexes.length - 1]];

        const requestBody = {
            userId: user.id,
            courtId: selectedCourtId,
            startTime: firstSlot.startTime,
            endTime: lastSlot.endTime,
            tournament: isTournament,
            matchId: matchId
        };

        try {
            const res = await axios.post('/api/reservations/create', requestBody);
            if (res.data.success) {
                setTimeout(() => {
                    if(isTournament && tournamentId){
                        navigate(`/tournaments/setup/${tournamentId}`);
                    } else {
                        navigate(`/reservations/${res.data.data.id}`, {state: { details: res.data.data }})
                    }
                    },500);
            } else {
                setError(res.data.message);
            }
        } catch (err) {
            setError("Server error while creating reservation.");
        }
    };

    return (
        <div className="reservation-container">
            <h2>{isTournament ? "Tournament reservation" : "Reserve court"}</h2>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            {/* Court choice, if not chosen already*/}
            {isTournament && !urlCourtId && (
                <div className="court-selector">
                    <label>Choose court:</label>
                    <select
                        value={selectedCourtId || ""}
                        onChange={(e) => {
                            setSelectedCourtId(e.target.value);
                            setIsInitialDateSet(false); // Block
                            setSlots([]); // Clearing slots to be safe
                        }}
                    >
                        <option value="">-- Choose --</option>
                        {availableCourts.map(c => (
                            <option key={c.id} value={c.id}>{c.name} (nr {c.courtNumber})</option>
                        ))}
                    </select>
                </div>
            )}

            {/* Calendar navigation */}
            <div className="date-nav">
                <button onClick={() => changeDay(-1)}>&lt;</button>
                <input
                    type="date"
                    value={selectedDate}
                    min={getTomorrowDate()}
                    onChange={(e) => setSelectedDate(e.target.value)}
                />
                <button onClick={() => changeDay(1)}>&gt;</button>
            </div>

            {/* Slot grid */}
            <div className="slot-grid">
                {loading ? <p>Loading slots...</p> : (
                    slots.map((slot, index) => {
                        const isSelected = selectedSlotIndexes.includes(index);
                        const isOccupied = !slot.available;
                        return (
                            <div
                                key={index}
                                className={`slot-item ${isOccupied ? 'occupied' : ''} ${isSelected ? 'selected' : ''}`}
                                onClick={() => !isOccupied && handleSlotClick(index)}
                            >
                                {slot.startTime.split('T')[1].substring(0, 5)}
                            </div>
                        );
                    })
                )}
            </div>

            {error && <p className="error-msg">{error}</p>}

            <button
                className="submit-btn"
                disabled={selectedSlotIndexes.length === 0}
                onClick={handleSubmit}
            >
                {isTournament ? "Confirm for tournament match" : "Reserve and pay"}
            </button>
        </div>
    );
};

export default CreateReservation;