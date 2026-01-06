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
                    <Route path ="/" element= {user ? (
                        <h1>Welcome. {user.firstName}!</h1>
                    ) : (
                        <Navigate to = "/login"/>
                    )}/>
                </Routes>
            </div>
        </BrowserRouter>
    )
}

export default App