import React,{Component} from 'react';
class UserItem extends Component{
    constructor(props){
        super(props);
        this.state={
            item:props.item,
            data:props.data
        }
    }
    render(){
        return(

        	
            <li>{this.state.data.link}</li>
        )
    }
}
export default UserItem;