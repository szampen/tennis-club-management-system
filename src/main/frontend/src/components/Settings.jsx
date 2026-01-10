import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Settings.css';

const Settings = ({ user, setUser }) => {
    const navigate = useNavigate();
    const [isDeleting, setIsDeleting] = useState(false);
    const [info, setInfo] = useState({ message: '', isSuccess: false });

    const handleUpdate = async (field, value, endpoint = '/api/users/update-profile') => {
        try {
            const payload = { [field]: value };
            const res = await axios.put(endpoint, payload);

            if (res.data.success) {
                if (field === 'email' || field === 'password') {
                    setInfo({ message: "Security changed. Please login again.", isSuccess: true });
                    setTimeout(() => {
                        setUser(null);
                        navigate('/login');
                    }, 2000);
                } else {
                    // Refresh local user data from session
                    const me = await axios.get('/api/users/me');
                    setUser(me.data.data);
                }
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err) {
            setInfo({ message: "Error updating data.", isSuccess: false });
        }
    };

    const handleDelete = async () => {
        if (await axios.delete('/api/users/delete-account')) {
            setUser(null);
            navigate('/');
        }
    };

    if (!user) return <div className="loading">Access Denied.</div>;

    return (
        <div className="settings-container">
            <button className="btn-back" onClick={() => navigate(-1)}>← Back</button>
            <h1 className="settings-title">Account Settings</h1>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <div className="settings-grid">
                {/* Left side : profile data */}
                <div className="settings-card glass-card">
                    <h3>Profile Information</h3>
                    <SettingRow label="First Name" value={user.firstName} onSave={(val) => handleUpdate('firstName', val)} />
                    <SettingRow label="Last Name" value={user.lastName} onSave={(val) => handleUpdate('lastName', val)} />
                    <SettingRow label="Phone" value={user.phoneNumber} onSave={(val) => handleUpdate('phoneNumber', val)} />
                </div>

                {/* Right side: security vulnerable data */}
                <div className="settings-card glass-card security-card">
                    <h3>Security</h3>

                    <div className="security-info-banner">
                        <p>Changing your <strong>email</strong> or <strong>password</strong> will require you to log in again for security reasons.</p>
                    </div>

                    <SettingRow label="Email" value={user.email} onSave={(val) => handleUpdate('email', val, '/api/users/change-email')} />
                    <SettingRow label="Password" value="********" type="password" onSave={(val) => handleUpdate('password', val, '/api/users/change-password')} />

                    <div className="danger-zone">
                        {!isDeleting ? (
                            <button className="btn-delete-init" onClick={() => setIsDeleting(true)}>Delete Account</button>
                        ) : (
                            <div className="confirm-delete">
                                <span>Are you absolutely sure?</span>
                                <button className="btn-confirm-delete" onClick={handleDelete}>Yes, Delete</button>
                                <button className="btn-cancel-delete" onClick={() => setIsDeleting(false)}>Cancel</button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

// Helper component for row
const SettingRow = ({ label, value, onSave, type = "text" }) => {
    const [edit, setEdit] = useState(false);
    const [val, setVal] = useState(type === "password" ? "" : value);

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

export default Settings;