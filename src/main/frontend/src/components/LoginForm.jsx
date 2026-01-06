import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate, Link} from 'react-router-dom';

const LoginForm = ({onLoginSuccess}) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post('api/users/login', {email,password});
            if (res.data.success){
                onLoginSuccess(res.data.data);
                navigate('/');
            } else {
                alert(res.data.message);
            }
        } catch (err){
            alert("Logging error");
        }
    }

    return (
        <div>
            <h2>Log in</h2>
            <form onSubmit={handleSubmit}>
                <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required/>
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required />
                <button type="submit">Log in</button>
            </form>
            <p>You don't have an account? <Link to = "/register">Register</Link></p>
        </div>
    )
}

export default LoginForm

