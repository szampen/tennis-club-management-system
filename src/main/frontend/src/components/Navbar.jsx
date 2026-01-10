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
            <div className="navbar-section left">
                <Link to="/" className="nav-logo">TENNIS CLUB</Link>
            </div>

            <div className="navbar-section center">
                <Link to="/players" className="nav-link-main">Find Players</Link>
                <Link to="/courts/filtered" className="nav-link-main">Find Courts</Link>
            </div>

            <div className="navbar-section right">
                {user ? (
                    <div className="user-info">
                        <Link to={`/user/${user.id}`} className="nav-welcome">Hi, {user.firstName}!</Link>
                        <button className="btn-logout" onClick={logout}>Logout</button>
                    </div>
                ) : (
                    <div className="auth-links">
                        <Link to="/login" className="nav-link-auth">Login</Link>
                        <Link to="/register" className="btn-register">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    )
}

export default Navbar