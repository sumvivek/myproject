import React, { Component} from 'react';
import axios from 'axios';
import UserItem from './UserItem';
import Suggestions from './Suggestions';
class Search extends Component {
	constructor(){
        super();
        this.state={
            users:[]
        }
    }
	
	 getuser(user,query){
		 
		 axios.get('http://localhost:8080/search/'+query+'').then(({ data }) => {
		        this.setState({
		            users: data.data
		          })
		        })
	        /*axios.request({
	            method:'GET',
	            url:'http://localhost:8080/search/'+query+'',
	            data:user
	        }).then(res=>{
	            this.props.history.push('/')
	        }).catch(err=>console.log(err));
	        */
	    }
	    onAddSubmit(){
	        const  user={

	            query:this.refs.query.value
	        


	        }
	        var query = this.refs.query.value;
	        this.getuser(user,query);
	        console.log("success query", this.refs.query.value);
	        console.log("success");
	    }
	    
    render() {
    	 
           
        
    	return(
    			<div class="wrap">
    			<form onSubmit={this.onAddSubmit.bind(this)}>
    			<div class="search">
    		      <input type="text" class="searchTerm" placeholder="What are you looking for?" name="query" ref="query"/>
    		   
    		      <button type="submit" class="searchButton">
    		        <i class="fa fa-search"></i>
    		     </button>
    		 	  <Suggestions users={this.state.users} /> 
    		   </div>
    		  
    			</form>
    			</div>
    			 
    		)	
    		

    }
}

export default Search;

