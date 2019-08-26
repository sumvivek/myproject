import React, { Component} from 'react';
import {  Link, NavLink} from 'react-router-dom';
import AddUser from './components/AddUser';
import Login from './components/Login';
import { Switch, Route } from 'react-router';
import './App.css';

class App extends Component {
    render() {
        return (
//            <Router basename="/react-auth-ui/">
                <div className="App">
                    <div className="App__Aside"></div>
                    <div className="App__Form">
                        <div className="PageSwitcher">
                            <NavLink to="/" activeClassName="PageSwitcher__Item--Active" className="PageSwitcher__Item">Sign In</NavLink>
                            <NavLink exact to="/sign-up" activeClassName="PageSwitcher__Item--Active" className="PageSwitcher__Item">Sign Up</NavLink>
                        </div>

                        <div className="FormTitle">
                            <NavLink to="/ " activeClassName="FormTitle__Link--Active" className="FormTitle__Link">Sign In</NavLink> or <NavLink exact to="/sign-up" activeClassName="FormTitle__Link--Active" className="FormTitle__Link">Sign Up</NavLink>
                        </div>
                        <switch>
                        <Route exact path="/" component={Login}>
                        </Route>
                        <Route exact path="/sign-up" component={AddUser}>
                        
                        </Route>
                        </switch>
                    </div>

                </div>
//            </Router>
        );
    }
}

export default App;

