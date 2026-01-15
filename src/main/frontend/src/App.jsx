import React, {useState, useEffect} from 'react';
import {Navigate, Route, HashRouter, Routes} from "react-router-dom";
import axios from 'axios';
import Navbar from "./components/Navbar";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import UserProfile from "./components/UserProfile.jsx";
import ReservationDetails from "./components/ReservationDetails";
import Settings from "./components/Settings.jsx";
import PlayersList from "./components/PlayersList.jsx";
import CourtList from "./components/CourtList.jsx";
import CourtEdit from "./components/CourtEdit.jsx";
import CourtCreate from "./components/CourtCreate.jsx";
import CreateReservation from "./components/CreateReservation.jsx";
import TournamentList from "./components/TournamentList.jsx";
import TournamentSetupMatches from "./components/TournamentSetupMatches.jsx";
import TournamentDetails from "./components/TournamentDetails.jsx";
import MatchDetails from "./components/MatchDetails.jsx";
axios.defaults.baseURL = 'http://localhost:9000';
axios.defaults.withCredentials = true;

function App(){
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const savedUser = sessionStorage.getItem('loggedUser');
        if (savedUser) {
            try {
                setUser(JSON.parse(savedUser));
            } catch (e) {
                console.error("Error parsing user.", e);
                sessionStorage.removeItem('loggedUser');
            }
        }
        setLoading(false); // Finish loading, not regarding the result
    }, []);

    if(loading) return <div>Loading the system...</div>

    return (
        <HashRouter>
            <Navbar user={user} setUser={setUser} />
            <div style = {{padding: '20px'}}>
                <Routes>
                    <Route path="/login" element = {!user ? <LoginForm onLoginSuccess={setUser}/> : <Navigate to="/"/>}/>
                    <Route path="/register" element={!user ? <RegisterForm /> : <Navigate to="/" />} />
                    <Route path="/settings" element={<Settings user = {user} setUser={setUser}/>} />
                    <Route path="/user/:id" element={<UserProfile currentUser={user} />} />
                    <Route path="/reservations/:id" element={user ? <ReservationDetails /> : <Navigate to="/login" />} />
                    <Route path="/players" element={<PlayersList />} />
                    <Route path="/courts/filtered" element={<CourtList user = {user}/>} />
                    <Route path="/courts/new" element={<CourtCreate user={user} />} />
                    <Route path="/courts/edit/:id" element={<CourtEdit user={user} />} />
                    <Route path="/reserve/:courtId" element={user ? <CreateReservation user={user} /> : <Navigate to="/login" />}/>
                    <Route path="/reserve" element={user ? <CreateReservation user={user} /> : <Navigate to="/login" />}/>
                    <Route path="/tournaments" element={<TournamentList user={user} />} />
                    <Route path="/tournaments/setup/:id" element={<TournamentSetupMatches />} />
                    <Route path="/tournaments/:id" element={<TournamentDetails user={user} />} />
                    <Route path="/match/:matchId" element={<MatchDetails user={user} />} />
                    <Route path="/" element={
                        user ? (
                            <div style={{textAlign: 'center', marginTop: '50px'}}>
                                <h1>Welcome back, {user.firstName}! 🎾</h1>
                                <p>Ready for a match today?</p>
                            </div>
                        ) : (
                            <div style={{textAlign: 'center', marginTop: '50px'}}>
                                <h2>Welcome to the Tennis Club</h2>
                                <p>Join us and feel the spirit of the courts.</p>
                            </div>
                        )
                    }/>
                </Routes>
            </div>
        </HashRouter>
    )
}

export default App