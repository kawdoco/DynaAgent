import React, { Component } from "react";
import PropTypes, { func } from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button";
import { Container, Row, Col } from "react-grid-system";
import { formatDate } from "../../Global/DateTimeUtil";
import WorkCenterHeaderForm from "./WorkCenterHeaderForm";
import WorkCenterInterruptionsTable from "./WorkCenterInterruptionsTable";

import { getWCDetails } from "../../Services/WorkCenterService";

import MenuAppBar from "../MenuAppBar/MenuAppBar";

const styles = theme => ({
  root: {
    flexGrow: 1
  },
  fab: {}
});

class WorkCenterHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      workCenterNos: [],
      workCenters: [],
      selectedWorkCenterNo: 0,
      currentWorkCenter: {}
    };
  }

  componentDidMount() {
    getWCDetails().then(res => {
      // get the service data
      const serviceData = res.data;
      // send the service data to be formatted
      this.formatWorkCenterData(serviceData);
    });
    document.title = "Work Centre Details";
  }

  formatWorkCenterData(workCenterData) {
    var currentData;
    if (workCenterData != null) {
      currentData = workCenterData;
    }
    console.log(currentData);

    const workCenterNos = [];
    const workCenters = [];
    // Push first default order no.
    workCenterNos.push({
      value: -1,
      label: "Select Work Center No"
    });
    for (var i = 0; i < currentData.length; i++) {
      var workCenterObj = currentData[i];

      workCenterNos.push({
        value: workCenterObj.workCenterNo,
        label: workCenterObj.workCenterNo
      });
      workCenters.push(workCenterObj);
    }

    console.log(workCenterNos);

    this.setState({ workCenterNos, workCenters });
  }

  onWorkCenterNoChanged = name => event => {
    var selectedWorkCenterNo = event.target.value;
    this.setState({
      selectedWorkCenterNo: selectedWorkCenterNo
    });

    var filteredData = this.state.workCenters.filter(
      field => field.workCenterNo == selectedWorkCenterNo
    );
    console.log(filteredData[0]);
    var currentWorkCenter = {};
    if (filteredData[0]) {
      currentWorkCenter = filteredData[0];
    }
    this.setState({
      currentWorkCenter: currentWorkCenter
    });
  };

  render() {
    const { classes } = this.props;
    const {
      workCenterNos,
      workCenters,
      selectedWorkCenterNo,
      currentWorkCenter
    } = this.state;
    return (
      <div className="App">
        <MenuAppBar
          titleText="Work Center"
          workCenterNos={workCenterNos}
          currentWorkCenterNo={selectedWorkCenterNo}
          workCenterNoChangedEvent={this.onWorkCenterNoChanged}
        />

        <main id="page-wrap">
          <h1>Work Center</h1>
          <div>
            <Row>
              <WorkCenterHeaderForm workCenterDetails={currentWorkCenter} />
            </Row>
            <Row>&nbsp;</Row>
            <Row>
              <WorkCenterInterruptionsTable
                workCenterDetails={currentWorkCenter}
                workCenterInterruptions={
                  currentWorkCenter.workCenterInterruptions
                }
              />
            </Row>
          </div>
        </main>
      </div>
    );
  }
}

WorkCenterHeader.propTypes = {
  classes: PropTypes.object.isRequired
};
export default withStyles(styles)(WorkCenterHeader);
