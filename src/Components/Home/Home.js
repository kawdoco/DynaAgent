import React, { Component } from "react";
import Background from "../../Images/bg.png"
import "./Home.css";

/* Components */
import MenuAppBar from "../MenuAppBar/MenuAppBar";

var sectionStyle = {
  backgroundImage: `url(${Background})`
};



export default class Home extends Component {
  componentDidMount(){
    document.title = "DynoSchedule : Home";
  }
  render() {
    return (
      <div className="App">
      <MenuAppBar titleText= "Home"/>
        <main id="page-wrap" style={ sectionStyle }>
          <h2>Hi, Prabash!</h2>
          <h1>Welcome to DynoScheduler </h1>
        </main>
      </div>
    );
  }
}
