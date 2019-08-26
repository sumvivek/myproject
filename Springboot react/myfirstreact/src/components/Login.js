import React,{Component} from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';


class Login extends Component{
    getuser(user){
        axios.request({
            method:'GET',
            url:'http://localhost:8080/user/{username}/{password}',
            data:user
        }).then(res=>{
            this.props.history.push('/')
        }).catch(err=>console.log(err));
    }
    onAddSubmit(){
        const  user={

            username:this.refs.username.value,

            password:this.refs.password.value


        }
        this.getuser(user);
        console.log("success");
    }
    render(){
        return(
            <div className="FormCenter">
                <form onSubmit={this.onAddSubmit.bind(this)}>

                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="username">Username</label>
                        <input type="text" className="FormField__Input" name="username" placeholder="username" ref="username"/>
                    </div>
                    <div className="FormField">
                        <label className="FormField__Label" htmlFor="password">Password</label>
                        <input type="password" className="FormField__Input" name="password" placeholder="password" ref="password"/>
                    </div>
                    <div className="FormField">
                        <button className="FormField__Button mr-20">Sign In</button> <Link to="/sign-up" className="FormField__Link">Create an account</Link>
                    </div>
                </form>

            </div>
        )
    }
}
export default Login;