import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Settings.css';

const CourtCreate = ({ user }) => {
    const navigate = useNavigate();
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    // Init state for new court
    const [form, setForm] = useState({
        name: '',
        courtNumber: '',
        surfaceType: 'CLAY',
        hasRoof: false,
        location: '',
        pricePerHour: '',
        availableForReservations: true
    });

    if (!user || user.userType !== 'ADMIN') return <div className="loading">Access Denied.</div>;

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post('/api/courts', form);

            if (res.data.success) {
                setInfo({ message: "Court created successfully! Redirecting...", isSuccess: true });
                setTimeout(() => {
                    navigate('/courts/filtered');
                }, 2000);
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err) {
            setInfo({ message: "Error creating court. Check if number is unique.", isSuccess: false });
        }
    };

    const handleChange = (field, value) => {
        setForm(prev => ({ ...prev, [field]: value }));
    };

    return (
        <div className="settings-container">
            <button className="btn-back" onClick={() => navigate(-1)}>← Back</button>
            <h1 className="settings-title">Add New Court</h1>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <div className="settings-grid" style={{ gridTemplateColumns: '1fr' }}>
                <form className="settings-card glass-card" onSubmit={handleSubmit} style={{ maxWidth: '600px', margin: '0 auto' }}>
                    <h3>Court Details</h3>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Name</label>
                            <input
                                type="text"
                                placeholder="e.g. Center Court"
                                value={form.name}
                                onChange={e => handleChange('name', e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Court Number</label>
                            <input
                                type="number"
                                placeholder="1"
                                value={form.courtNumber}
                                onChange={e => handleChange('courtNumber', e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Surface Type</label>
                            <select
                                value={form.surfaceType}
                                onChange={e => handleChange('surfaceType', e.target.value)}
                                style={{ padding: '8px', borderRadius: '5px', marginTop: '5px' }}
                            >
                                <option value="CLAY">Clay</option>
                                <option value="GRASS">Grass</option>
                                <option value="HARD">Hard</option>
                                <option value="CARPET">Carpet</option>
                            </select>
                        </div>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Location</label>
                            <input
                                type="text"
                                placeholder=""
                                value={form.location}
                                onChange={e => handleChange('location', e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Price Per Hour (PLN)</label>
                            <input
                                type="number"
                                placeholder="50"
                                value={form.pricePerHour}
                                onChange={e => handleChange('pricePerHour', e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="setting-row">
                        <div className="row-info">
                            <label>Options</label>
                            <div style={{ display: 'flex', gap: '20px', marginTop: '10px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '5px', textTransform: 'none' }}>
                                    <input
                                        type="checkbox"
                                        checked={form.hasRoof}
                                        onChange={e => handleChange('hasRoof', e.target.checked)}
                                    /> Has Roof
                                </label>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '5px', textTransform: 'none' }}>
                                    <input
                                        type="checkbox"
                                        checked={form.availableForReservations}
                                        onChange={e => handleChange('availableForReservations', e.target.checked)}
                                    /> Available
                                </label>
                            </div>
                        </div>
                    </div>

                    <div style={{ marginTop: '30px' }}>
                        <button type="submit" className="btn-save" style={{ width: '100%', padding: '12px' }}>
                            CREATE COURT
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CourtCreate;