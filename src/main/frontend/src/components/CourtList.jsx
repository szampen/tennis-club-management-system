import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/courtlist.css';

const CourtList = ({ user }) => {
    const navigate = useNavigate();
    const [courts, setCourts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deletingId, setDeletingId] = useState(null);
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    const [filters, setFilters] = useState({
        surfaceType: null,
        hasRoof: null,
        availableForReservations: true,
        courtSort: 'COURTNUMBER',
        direction: 'ASC'
    });

    const isAdmin = user && user.userType === 'ADMIN';

    const fetchCourts = useCallback(async () => {
        setLoading(true);
        try {
            const payload = {
                courtSort: filters.courtSort || 'COURTNUMBER',
                direction: filters.direction || 'ASC'
            };

            if (filters.surfaceType) payload.surfaceType = filters.surfaceType;
            if (filters.hasRoof !== null) payload.hasRoof = filters.hasRoof;
            if (filters.availableForReservations !== null) {
                payload.availableForReservations = filters.availableForReservations;
            }

            const res = await axios.post('/api/courts/filtered', payload);
            if (res.data && res.data.success) {
                setCourts(res.data.data);
            }
        } catch (err) {
            setInfo({ message: "Error loading courts", isSuccess: false });
        } finally {
            setLoading(false);
        }
    }, [filters]);

    useEffect(() => {
        fetchCourts();
    }, []);

    const handleDelete = async (courtId) => {
        try {
            const res = await axios.delete(`/api/courts/${courtId}`);
            if (res.data.success) {
                setDeletingId(null);
                fetchCourts();
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err) {
            setInfo({ message: "Error deleting", isSuccess: false });
        }
    };

    return (
        <div className="court-page-container">

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <aside className="court-sidebar">
                {isAdmin && (
                    <button className="btn-create-court" onClick={() => navigate('/courts/new')}>
                        + CREATE COURT
                    </button>
                )}

                <div className="filter-section">
                    <h3>Filters</h3>

                    <label>Surface</label>
                    <select
                        value={filters.surfaceType || ""}
                        onChange={e => setFilters(prev => ({...prev, surfaceType: e.target.value === "" ? null : e.target.value}))}
                    >
                        <option value="">Any</option>
                        <option value="GRASS">Grass</option>
                        <option value="CLAY">Clay</option>
                        <option value="HARD">Hard</option>
                        <option value="CARPET">Carpet</option>
                    </select>

                    <label>Roof</label>
                    <select
                        value={filters.hasRoof === null ? "" : filters.hasRoof.toString()}
                        onChange={e => setFilters(prev => ({...prev, hasRoof: e.target.value === "" ? null : e.target.value === "true"}))}
                    >
                        <option value="">Any</option>
                        <option value="true">Covered</option>
                        <option value="false">Open Air</option>
                    </select>

                    <label>Available For Reservations</label>
                    <select
                        value={filters.availableForReservations === null ? "" : filters.availableForReservations.toString()}
                        onChange={e => setFilters(prev => ({...prev, availableForReservations: e.target.value === "" ? null : e.target.value === "true"}))}
                    >
                        <option value="">Any</option>
                        <option value="true">Available</option>
                        <option value="false">Closed</option>
                    </select>

                    <label>Sort By</label>
                    <div className="sort-group">
                        <select
                            className="sort-select"
                            value={filters.courtSort}
                            onChange={e => setFilters(prev => ({...prev, courtSort: e.target.value}))}
                        >
                            <option value="COURTNUMBER">Court Number</option>
                            <option value="FIRSTDATE">Availability Date</option>
                        </select>

                        <button
                            type="button"
                            className={`btn-direction ${filters.direction.toLowerCase()}`}
                            onClick={() => setFilters(prev => ({
                                ...prev,
                                direction: prev.direction === 'ASC' ? 'DESC' : 'ASC'
                            }))}
                        >
                            {filters.direction === 'ASC' ? '↑' : '↓'}
                        </button>
                    </div>

                    <button className="btn-apply-filters" disabled={loading} onClick={fetchCourts}>
                        {loading ? "Loading..." : "Apply Changes"}
                    </button>
                </div>
            </aside>

            <main className="court-main-content">
                {loading && courts.length === 0 ? (
                    <div className="loader">Searching for available courts...</div>
                ) : (
                    <div className="court-scroll-list">
                        {courts.length === 0 && !loading && <div className="no-courts">No courts found.</div>}
                        {courts.map(court => (
                            <div key={court.id} className="court-card glass-card">
                                <div className="court-details">
                                    <div className="court-header">
                                        <h2>{court.name} <span className="c-number">#{court.courtNumber}</span></h2>
                                        {court.hasRoof && <span className="roof-badge">🏠 Roofed</span>}
                                    </div>
                                    <div className="info-grid">
                                        <div className="info-item"><label>Location:</label> <span>{court.location}</span></div>
                                        <div className="info-item"><label>Surface:</label> <span>{court.surfaceType}</span></div>
                                        <div className="info-item">
                                            <label>Next Date:</label>
                                            <span className="date-highlight">{court.firstAvailableDate || "None soon"}</span>
                                        </div>
                                        <div className="info-item"><label>Price:</label> <span className="price-tag">{court.pricePerHour} PLN/h</span></div>
                                    </div>
                                </div>

                                <div className="court-actions">
                                    {court.availableForReservations && (
                                        <button className="btn-book" onClick={() => navigate(`/reserve/${court.id}`)}>
                                            MAKE RESERVATION
                                        </button>
                                    )}
                                    {isAdmin && (
                                        <>
                                            <button className="btn-update" onClick={() => navigate(`/courts/edit/${court.id}`)}>UPDATE</button>
                                            {deletingId === court.id ? (
                                                <div className="delete-confirmation-inline">
                                                    <span className="warning-text">Delete permanently?</span>
                                                    <div className="confirm-buttons">
                                                        <button className="btn-confirm-final" onClick={() => handleDelete(court.id)}>YES</button>
                                                        <button className="btn-cancel-delete" onClick={() => setDeletingId(null)}>NO</button>
                                                    </div>
                                                </div>
                                            ) : (
                                                <button className="btn-delete" onClick={() => setDeletingId(court.id)}>DELETE</button>
                                            )}
                                        </>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>
        </div>
    );
};

export default CourtList;