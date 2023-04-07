import React, { Component } from "react";
import PropTypes, { func } from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button";
import { Container, Row, Col } from "react-grid-system";
import { formatDate } from "../../Global/DateTimeUtil";
import PartHeaderForm  from "./PartHeaderForm"
import PartUnavailabilityTable  from "./PartUnavailabilityTable"

import { getPartDetails } from "../../Services/PartDetailsService";

import MenuAppBar from "../MenuAppBar/MenuAppBar";

const styles = theme => ({
  root: {
    flexGrow: 1
  },
  fab: {}
});

class PartHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      partNos: [],
      partDetails: [],
      selectedPartNo: 0,
      currentPart: {}
    };
  }

  componentDidMount() {
    getPartDetails().then(res => {
      // get the service data
      const serviceData = res.data;
      // send the service data to be formatted
      this.formatPartData(serviceData);
    });
    document.title = "Part Details";
  }

  formatPartData(partData) {
    var currentData;
    if (partData != null) {
      currentData = partData;
    }
    console.log(currentData);

    const partNos = [];
    const partDetails = [];
    // Push first default order no.
    partNos.push({
      value: -1,
      label: "Select Part No"
    });
    for (var i = 0; i < currentData.length; i++) {
      var partDataObj = currentData[i];

      partNos.push({
        value: partDataObj.partNo,
        label: partDataObj.partNo
      });
      partDetails.push(partDataObj);
    }

    console.log(partNos);

    this.setState({ partNos, partDetails });
  }

  onPartNoChanged = name => event => {
    var selectedPartNo = event.target.value;
    this.setState({
        selectedPartNo: selectedPartNo
    });

    var filteredData = this.state.partDetails.filter(
      field => field.partNo == selectedPartNo
    );
    console.log(filteredData[0]);
    var currentPart = {};
    if (filteredData[0]) {
        currentPart = filteredData[0];
    }
    this.setState({
        currentPart: currentPart
    });
  };

  render() {
    const { classes } = this.props;
    const {
      partNos,
      partDetails,
      selectedPartNo,
      currentPart
    } = this.state;
    return (
      <div className="App">
        <MenuAppBar
         titleText="Part Details"
         partNos={partNos}
         currentPartNo={selectedPartNo}
         partNoChangedEvent={this.onPartNoChanged} />

        <main id="page-wrap">
          <h1>Part Details</h1>
          <div>
            <Row>
              <PartHeaderForm
                partDetails={currentPart}
              />
            </Row>
            <Row>&nbsp;</Row>
            <Row>
              <PartUnavailabilityTable 
                partDetails={currentPart}
                partUnavailabilityDetails={currentPart.partUnavailabilityDetails}/>
            </Row>
          </div>
        </main>
      </div>
    );
  }
}

PartHeader.propTypes = {
  classes: PropTypes.object.isRequired
};
export default withStyles(styles)(PartHeader);
