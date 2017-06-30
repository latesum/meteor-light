import React, {PropTypes} from 'react';
import {connect} from 'react-redux';
import * as courseActions from '../../actions/courseActions';

class CoursesPage extends React.Component{
	constructor(props,context){
		super(props,context);
		this.state={'course':
			{'title':""}
		};
		this.onTitleChange=this.onTitleChange.bind(this);
		this.submitClick=this.submitClick.bind(this);
	}
	onTitleChange(event){
		let newCourse=this.state.course;
		newCourse.title=event.target.value;
		this.setState({'course':newCourse});
	}
	submitClick(){
		this.props.dispatch(courseActions.createCourse(this.state.course));
	}
	courseRow(course,index){
		return <div key={index}>{course.title}</div>;
	}
	render(){
		return(
			<div>
				<h1>Courses</h1>
				{this.props.courses.map(this.courseRow)}
				<h2>Add Courses</h2>
				<input
				  type="text"
				  value={this.state.course.title}
				  onChange={this.onTitleChange} />
				<input
				  type="submit"
				  value="submit"
				  onClick={this.submitClick} />
			</div>
		);
	}
}

function mapStateToProps(state, ownProps){
	return {
		courses:state.courses
	};

}

CoursesPage.propTypes={
	dispatch:PropTypes.func,
	courses:PropTypes.array
};

export default connect(mapStateToProps)(CoursesPage);