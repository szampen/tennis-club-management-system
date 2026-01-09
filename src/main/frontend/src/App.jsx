import React, {useState, useEffect} from 'react';
import {Navigate, Route, BrowserRouter, Routes} from "react-router-dom";
import axios from 'axios';
import Navbar from "./components/Navbar";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
axios.defaults.withCredentials = true;

function App(){
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('api/users/me')
            .then(res => setUser(res.data.data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false))
    }, []);

    if(loading) return <div>Loading the system...</div>

    return (
        <BrowserRouter>
            <Navbar user={user} setUser={setUser} />
            <div style = {{padding: '20px'}}>
                <Routes>
                    <Route path="/login" element = {!user ? <LoginForm onLoginSuccess={setUser}/> : <Navigate to="/"/>}/>
                    <Route path="/register" element={!user ? <RegisterForm /> : <Navigate to="/" />} />
                    <Route path="/" element={
                        user ? (
                            <div style={{textAlign: 'center', marginTop: '50px'}}>
                                <h1>Welcome back, {user.firstName}! 🎾</h1>
                                <p>Ready for a match today?</p>
                            </div>
                        ) : (
                            <div style={{textAlign: 'center', marginTop: '50px'}}>
                                <h2>Welcome to the Tennis Club</h2>
                                <p>Join us and feel the spirit of the grass courts.</p>
                            </div>
                        )
                    }/>
                </Routes>
            </div>
        </BrowserRouter>
    )
}

export default App