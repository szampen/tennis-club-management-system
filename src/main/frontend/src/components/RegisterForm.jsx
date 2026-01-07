import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";

const RegisterForm = () => {
    const [formData, setFormData] = useState({
        email: '', password: '', firstName: '', lastName: '', phoneNumber: ''
    });
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        const res = await axios.post('api/users/register', formData);
        if(res.data.success){
            navigate('/login');
        }
    };

    return (
        <div>
            <h2>Registration</h2>
            <form onSubmit={handleRegister}>
                <input type="text" placeholder="First Name" onChange={e => setFormData({...formData, firstName: e.target.value})} />
                <input type="text" placeholder="Last Name" onChange={e => setFormData({...formData, lastName: e.target.value})} />
                <input type="email" placeholder="Email" onChange={e => setFormData({...formData, email: e.target.value})} />
                <input type="password" placeholder="Password" onChange={e => setFormData({...formData, password: e.target.value})} />
                <input type="text" placeholder="Phone Number" onChange={e => setFormData({...formData, phoneNumber: e.target.value})} />
                <button type="submit">Create account</button>
            </form>
        </div>
    )
}

export default RegisterForm