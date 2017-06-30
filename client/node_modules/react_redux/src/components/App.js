import React, {PropTypes} from 'react';
import Header from './common/Header';

class App extends React.Component{
	render(){
		return(
			<div className="container-fluid">
			   <h1>React and Redux in ES6 on Pluralsight</h1>
			   <Header/>
			  {this.props.children}
			</div>
		);
	}
}

App.propTypes={
	children: PropTypes.object.isRequired
};

export default App;