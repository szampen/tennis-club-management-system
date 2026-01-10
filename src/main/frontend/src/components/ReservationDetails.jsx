import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/ReservationDetails.css';

const ReservationDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [details, setDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isConfirming, setIsConfirming] = useState(false);

    const fetchDetails = () => {
        setLoading(true);
        axios.get(`/api/reservations/${id}`)
            .then(res => {
                setDetails(res.data.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Error fetching reservation details", err);
                setError("Reservation not found.");
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchDetails();
    }, [id]);

    const handleCancel = async () => {
        try {
            const response = await axios.post(`/api/reservations/${id}/cancel`);
            if (response.data.success) {
                setIsConfirming(false);
                fetchDetails();
            } else {
                console.error(response.data.message);
                setIsConfirming(false);
            }
        } catch (err) {
            console.error("Cancel error", err);
            setIsConfirming(false);
        }
    };

    if (loading) return <div className="loading">Loading details...</div>;
    if (!details || error) return <div className="error">Reservation not found.</div>;

    return (
        <div className="reservation-container">
            <div className="top-actions">
                <button className="btn-back" onClick={() => navigate(-1)}>← Back to Profile</button>

                {details.status === "ACTIVE"&& (
                    <div className="cancel-container">
                        {!isConfirming ? (
                            // First stage: main button
                            <button
                                className="btn-cancel-init"
                                onClick={() => setIsConfirming(true)}
                            >
                                Cancel Reservation
                            </button>
                        ) : (
                            // Second stage: confirmation
                            <div className="confirm-group">
                                <span className="confirm-text">Are you sure?</span>
                                <button className="btn-confirm-yes" onClick={handleCancel}>Yes</button>
                                <button className="btn-confirm-no" onClick={() => setIsConfirming(false)}>No</button>
                            </div>
                        )}
                    </div>
                )}
            </div>

            <div className="reservation-card glass-card">
                <div className="card-header">
                    <h2>Reservation #{details.id}</h2>
                    <span className={`status-badge ${details.status.toLowerCase()}`}>
                        {details.status}
                    </span>
                </div>

                <div className="details-grid">
                    {/* Time*/}
                    <div className="info-section">
                        <h3>Schedule</h3>
                        <p><strong>Date:</strong> {details.startTime.split('T')[0]}</p>
                        <p><strong>Time:</strong> {details.startTime.split('T')[1].substring(0,5)} - {details.endTime.split('T')[1].substring(0,5)}</p>
                    </div>

                    {/* Court */}
                    <div className="info-section">
                        <h3>Court Info</h3>
                        <p><strong>Name:</strong> {details.courtName} (No. {details.courtNumber})</p>
                        <p><strong>Surface:</strong> {details.courtSurface}</p>
                        <p><strong>Roof:</strong> {details.courtHasRoof ? "Yes" : "No"}</p>
                        <p><strong>Location:</strong> {details.courtLocation}</p>
                    </div>

                    {/* Payment */}
                    {details.payment && (
                        <div className="info-section payment-box">
                            <h3>Payment Details</h3>
                            <p><strong>Amount:</strong> {details.payment.amount}</p>
                            <p><strong>Status:</strong> {details.payment.status}</p>
                            <p><strong>Date:</strong> {details.payment.paymentDate}</p>
                            <p><strong>Transaction Id:</strong> {details.payment.transactionId}</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ReservationDetails;