import {Link, useNavigate} from 'react-router-dom';
import axios from 'axios';
import '../styles/navbar.css'

function Navbar({user,setUser}){
    const navigate = useNavigate();

    const logout = async () => {
        try{
            await axios.post('/api/users/logout');
            setUser(null);
            navigate('/');
        } catch (e){
            console.error("Logout failed", e);
        }
    }

    return (
        <nav className="navbar">
            <Link to="/" className="nav-logo">TENNIS CLUB</Link>
            <div className="nav-links">
                {user ? (
                    <div className="user-info">
                        <Link to={`/user/${user.id}`} className="nav-welcome">Hi, {user.firstName}!</Link>
                        <button className="btn-primary" onClick={logout}>Logout</button>
                    </div>
                ) : (
                    <div>
                        <Link to="/login">Login</Link>
                        <Link to="/register">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    )
}

export default Navbar