import {Link, useNavigate} from 'react-router-dom';
import axios from 'axios';

function Navbar({user,setUser}){
    const navigate = useNavigate();

    const logout = async () => {
        try{
            await axios.post('api/users/logout');
            setUser(null);
            navigate('/login');
        } catch (e){
            console.error("Logout failed", e);
        }
    }

    return (
        <nav style={{ background: '#333', color: '#fff', padding: '10px', display: 'flex', justifyContent: 'space-between' }}>
            <div>
                <Link to="/">Tennis Club</Link>
            </div>

            <div>
                {user ? (
                    <div>
                        <span>Welcome, {user.firstName}!</span>
                        <button onClick={logout}>Logout</button>
                    </div>
                ) : (
                    <div>
                        <Link to="/login">Log in</Link>
                        <span> / </span>
                        <Link to="/register">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    )
}

export default Navbar