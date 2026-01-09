import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate, Link} from "react-router-dom";

const RegisterForm = () => {
    const [formData, setFormData] = useState({
        email: '', password: '', firstName: '', lastName: '', phoneNumber: ''
    });
    const [info, setInfo] = useState({ message: '', isSuccess: false });
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();

        const isAnyEmpty = Object.values(formData).some(value => value.trim() === '');
        if (isAnyEmpty) {
            setInfo({ message: "Please fill in all fields.", isSuccess: false });
            return;
        }

        setInfo({ message: '', isSuccess: false });
        try {
            const res = await axios.post('api/users/register', formData);
            if (res.data.success) {
                setInfo({ message: res.data.message + " Redirecting...", isSuccess: true });
                setTimeout(() => navigate('/login'), 2000);
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err) {
            setInfo({ message: "Registration failed. Try again.", isSuccess: false });
        }
    };

    return (
        <div className="auth-container">
            <h2>Create Account</h2>
            <form onSubmit={handleRegister} className="auth-form" noValidate>
                <input
                    type="text" placeholder="First Name" required
                    value={formData.firstName}
                    onChange={e => setFormData({ ...formData, firstName: e.target.value })}
                />
                <input
                    type="text" placeholder="Last Name" required
                    value={formData.lastName}
                    onChange={e => setFormData({ ...formData, lastName: e.target.value })}
                />
                <input
                    type="email" placeholder="Email" required
                    value={formData.email}
                    onChange={e => setFormData({ ...formData, email: e.target.value })}
                />
                <input
                    type="password" placeholder="Password" required
                    value={formData.password}
                    onChange={e => setFormData({ ...formData, password: e.target.value })}
                />
                <input
                    type="text" placeholder="Phone Number" required
                    value={formData.phoneNumber}
                    onChange={e => setFormData({ ...formData, phoneNumber: e.target.value })}
                />

                <button type="submit" className="btn-primary">Register</button>
            </form>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <p className="auth-switch">
                Already have an account? <Link to="/login">Log in</Link>
            </p>
        </div>
    )
}

export default RegisterForm