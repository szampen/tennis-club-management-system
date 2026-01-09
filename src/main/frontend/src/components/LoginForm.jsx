import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate, Link} from 'react-router-dom';

const LoginForm = ({onLoginSuccess}) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [info, setInfo] = useState({ message: '', isSuccess: false });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post('api/users/login', {email,password});
            if (res.data.success){
                onLoginSuccess(res.data.data);
                navigate('/');
            } else {
                setInfo({ message: res.data.message, isSuccess: false });
            }
        } catch (err){
            setInfo({ message: "Logging error. Please try again.", isSuccess: false });
        }
    }

    return (
        <div className="auth-container">
            <h2>Log in</h2>
            <form onSubmit={handleSubmit} className="auth-form" noValidate>
                <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required/>
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required />
                <button type="submit" className="btn-primary">Log in</button>
            </form>

            {info.message && (
                <div className={info.isSuccess ? "alert-success" : "alert-error"}>
                    {info.message}
                </div>
            )}

            <p className="auth-switch">You don't have an account? <Link to = "/register">Register</Link></p>
        </div>
    )
}

export default LoginForm

