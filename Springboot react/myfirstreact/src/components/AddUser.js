import React,{Component} from 'react';
import axios from 'axios';
import {Link} from "react-router-dom";
//import App from './src/App.css';


class AddUser extends Component{
    adduser(user){
        axios.request({
            method:'POST',
            url:'http://localhost:8080/user/add',
            data:user
        }).then(response=>{
            this.props.history.push('/')
        }).catch(err=>console.log(err));
    }
    onAddSubmit(){
        const  user={
            id:this.refs.id.value,
            username:this.refs.username.value,
            firstname:this.refs.firstname.value,
            lastname:this.refs.lastname.value,
            age:this.refs.age.value,
            password:this.refs.password.value

        }
        this.adduser(user);
    }
    render(){
        return(
            <div className="FormCenter">
                <form onSubmit={this.onAddSubmit.bind(this)}>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="id">Id</label>
                      <input type="number" className="FormField__Input" name="id" placeholder="id" ref="id"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="username">Username</label>
                        <input type="text" className="FormField__Input" name="username" placeholder="username" ref="username"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="firstname">FirstName</label>
                        <input type="text" className="FormField__Input" name="firstname" placeholder="firstname" ref="firstname"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="lastname">Lastname</label>
                        <input type="text" className="FormField__Input" name="lastname" placeholder="lastname" ref="lastname"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="age">Age</label>
                        <input type="text" className="FormField__Input" name="age" placeholder="age" ref="age"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="password">Password</label>
                        <input type="password" className="FormField__Input" name="password" placeholder="password" ref="password"/>
                    </div>

                    <div className="FormField">
                        <button className="FormField__Button mr-20">Sign Up</button> <Link to="/" className="FormField__Link">I'm already member</Link>
                    </div>
                </form>
            </div>
        )
    }
}
export default AddUser;