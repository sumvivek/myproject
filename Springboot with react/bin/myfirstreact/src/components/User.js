import React,{Component} from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import UserItem from './UserItem';
class User extends Component{
    constructor(){
        super();
        this.state={
            users:[]
        }
    }
    componentWillMount() {
        this.getusers();
    }

    getusers(){
        axios.get('http://localhost:8080/Alluser').then(response=>{
            this.setState({users:response.data},()=>{
                console.log(this.state);
            })

        })
 }
    render(){
         var userItem=this.state.users.map((user, i)=>{
             return (

                 <UserItem key={user.id} item={user}/>
             )
        })
        return(
            <div>
                <h1>
                    Users
                </h1>
                <ul>
                    {userItem}
                </ul>
                <Link to="/add">Register here</Link>

                <Link to="/">Back to login page</Link>
            </div>
        )
    }
}
export default User;