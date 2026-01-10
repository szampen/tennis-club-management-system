import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import '../styles/Settings.css';

const CourtEdit = ({ user }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [court, setCourt] = useState(null);
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    useEffect(() => {
        const fetchCourt = async () => {
            try {
                const res = await axios.get(`/api/courts/${id}`);
                if (res.data.success) {
                    setCourt(res.data.data);
                } else {
                    setInfo({ message: res.data.message, isSuccess: false });
                }
            } catch (err) {
                setInfo({ message: "Error loading court data", isSuccess: false });
            }
        };
        fetchCourt();
    }, [id]);

    const handleUpdate = async (field, value) => {
        try {
            const payload = {
                id: id,
                [field]: value
            };

            const res = await axios.put(`/api/courts/${id}/update`, payload);

            if (res.data.success) {
                setInfo({ message: "Court updated successfully!", isSuccess: true });
                setCourt(prev => ({ ...prev, [field]: value }));
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err) {
            setInfo({ message: "Error updating court.", isSuccess: false });
        }
    };

    if (!user || user.userType !== 'ADMIN') return <div className="loading">Access Denied.</div>;
    if (!court) return <div className="loading">Loading court data...</div>;

    return (
        <div className="settings-container">
            <button className="btn-back" onClick={() => navigate(-1)}>← Back</button>
            <h1 className="settings-title">Court Editor</h1>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <div className="settings-grid">
                <div className="settings-card glass-card">
                    <h3>Basic Information</h3>
                    <CourtSettingRow label="Court Name" value={court.name} onSave={(val) => handleUpdate('name', val)} />
                    <CourtSettingRow label="Court Number" value={court.courtNumber} type="number" onSave={(val) => handleUpdate('courtNumber', val)} />
                    <CourtSettingRow label="Location" value={court.location} onSave={(val) => handleUpdate('location', val)} />
                </div>

                <div className="settings-card glass-card">
                    <h3>Technical Details</h3>
                    <CourtSettingRow label="Price Per Hour (PLN)" value={court.pricePerHour} type="number" onSave={(val) => handleUpdate('pricePerHour', val)} />

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Surface Type</label>
                            <span>{court.surfaceType}</span>
                        </div>
                        <select
                            className="btn-edit"
                            style={{background: 'white', padding: '5px'}}
                            value={court.surfaceType}
                            onChange={(e) => handleUpdate('surfaceType', e.target.value)}
                        >
                            <option value="CLAY">Clay</option>
                            <option value="GRASS">Grass</option>
                            <option value="HARD">Hard</option>
                            <option value="CARPET">Carpet</option>
                        </select>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Roof Status</label>
                            <span>{court.hasRoof ? "Covered (Roofed)" : "Open Air"}</span>
                        </div>
                        <button
                            className="btn-edit"
                            onClick={() => handleUpdate('hasRoof', !court.hasRoof)}
                        >
                            {court.hasRoof ? "Remove Roof" : "Add Roof"}
                        </button>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Reservation Status</label>
                            <span style={{ color: court.availableForReservations ? '#27ae60' : '#e74c3c' }}>
                                {court.availableForReservations ? "● Online / Available" : "● Offline / Maintenance"}
                            </span>
                        </div>
                        <button
                            className={`btn-edit ${court.availableForReservations ? 'danger-btn' : ''}`}
                            onClick={() => handleUpdate('availableForReservations', !court.availableForReservations)}
                        >
                            {court.availableForReservations ? "Close Court" : "Open Court"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

const CourtSettingRow = ({ label, value, onSave, type = "text" }) => {
    const [edit, setEdit] = useState(false);
    const [val, setVal] = useState(value);

    return (
        <div className="setting-row">
            <div className="row-info">
                <label>{label}</label>
                {!edit ? <span>{value}</span> : <input type={type} value={val} onChange={e => setVal(e.target.value)} />}
            </div>
            {!edit ? (
                <button className="btn-edit" onClick={() => setEdit(true)}>Change</button>
            ) : (
                <div className="edit-actions">
                    <button className="btn-save" onClick={() => { onSave(val); setEdit(false); }}>Save</button>
                    <button className="btn-cancel" onClick={() => { setEdit(false); setVal(value); }}>Cancel</button>
                </div>
            )}
        </div>
    );
};

export default CourtEdit;