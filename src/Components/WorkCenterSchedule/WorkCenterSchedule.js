import React, { Component } from "react";
import Scheduler, { Resource } from "devextreme-react/scheduler";
import classNames from "classnames";
import { withStyles } from "@material-ui/core/styles";
import { Row } from "react-grid-system";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { shopOrderOperations } from "../../Services/test";
import {
  getScheduledOrdersByWorkCenters
} from "../../Services/ShopOrderService";

/* CSS */
import "devextreme/dist/css/dx.common.css";
import "devextreme/dist/css/dx.dark.css";

/* Components */
import MenuAppBar from "../MenuAppBar/MenuAppBar";

const styles = theme => ({
  searchContainer: {
    display: "flex",
    flexWrap: "wrap",
    marginLeft: theme.spacing.unit * 2,
    marginBottom: theme.spacing.unit * 2
  },
  button: {
    marginTop: theme.spacing.unit * 3.5,
    marginLeft: theme.spacing.unit * 5
  }
});

const currentDate = new Date(2018, 7, 6);
const views = [
  "timelineDay",
  "timelineWeek",
  "timelineWorkWeek",
  "timelineMonth"
];

const soGroups = ["workCenterNo"];
const groups = ["priority"];

class WorkCenterSchedule extends Component {
  constructor(props) {
    super(props);
    this.state = {
      schedulerHeight: 300,
      fromWorkCenterNo: 1,
      toWorkCenterNo: 5
    };
  }

  componentDidMount() {
    this.loadShopOrderOperationsByWorkCenters();
    document.title = "Work Centre Schedule";
  }

  loadShopOrderOperationsByWorkCenters() {
    var skip = this.state.fromWorkCenterNo - 1;
    var take = this.state.toWorkCenterNo - skip;

    getScheduledOrdersByWorkCenters(skip, take).then(res => {
      // get the service data
      const serviceData = res.data;
      // send the service data to be formatted
      this.formatShopOrderOperationData(serviceData);
    });
  }

  formatShopOrderOperationData(soOperationData) {
    var currentData;
    if (soOperationData == null) {
      currentData = shopOrderOperations;
    } else {
      console.log("NOT NULL!");
      currentData = soOperationData;
    }
    console.log(currentData);

    const soOperations = [];
    const shopOrders = [];
    const workCenters = [];

    for (var i = 0; i < currentData.length; i++) {
      var soObject = currentData[i];
      let shopOrder = {
        id: soObject.orderNo,
        text: "O" + soObject.orderNo + " : " + soObject.description,
        color: this.getRandomColor()
      };
      shopOrders.push(shopOrder);
      for (var j = 0; j < soObject.operations.length; j++) {
        var operationObj = soObject.operations[j];
        operationObj.appointmentHeader = "Order No: " + 
          soObject.orderNo +
          ", Op. No: " +
          operationObj.operationId;
        soOperations.push(operationObj);

        let workCenter = {
          id: operationObj.workCenterNo,
          text:
            "Work Center " +
            operationObj.workCenterNo +
            " : " +
            operationObj.workCenterType,
          color: this.getRandomColor()
        };

        var found = workCenters.some(function(field) {
          return field.id === operationObj.workCenterNo;
        });
        if (!found) {
          workCenters.push(workCenter);
        }
      }
    }
    console.log("+++++++++++++++++");
    console.log(soOperations);
    console.log(shopOrders);
    console.log(workCenters);

    this.setState({ soOperations, shopOrders, workCenters });

    // the height of the scheduler is set dynamically, for each of the work center 300 height is set
    var height = workCenters.length * 80;
    this.setState({ schedulerHeight: height });
  }

  getRandomColor() {
    var letters = "0123456789ABCDEF";
    var color = "#";
    for (var i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  }

  handleChange = name => event => {
    this.setState({
      [name]: event.target.value
    });
  };

  onLoadButtonPress = () => {
    this.loadShopOrderOperationsByWorkCenters();
  };

  render() {
    const { classes } = this.props;
    const {
      content,
      soOperations,
      shopOrders,
      workCenters,
      schedulerHeight
    } = this.state;
    return (
      <div className="App">
        <MenuAppBar titleText="Work Centre Schedule" />
        <main id="page-wrap">
          <h1>Work Centre Schedule</h1>
          <div className={classes.searchContainer}>
            <Row>
              <form noValidate autoComplete="off">
                <TextField
                  id="from-work-center"
                  label="From Work Center ID"
                  defaultValue=" "
                  value={this.state.fromWorkCenterNo}
                  onChange={this.handleChange("fromWorkCenterNo")}
                  margin="normal"
                  variant="standard"
                />
                <TextField
                  id="to-work-center"
                  label="To Work Center ID"
                  defaultValue=" "
                  value={this.state.toWorkCenterNo}
                  onChange={this.handleChange("toWorkCenterNo")}
                  margin="normal"
                  variant="standard"
                />
                <Button
                  variant="contained"
                  color="primary"
                  className={classes.button}
                  onClick={() => this.onLoadButtonPress()}
                >
                  Load Details
                </Button>
              </form>
            </Row>
          </div>
          <div style={{ height: "75%" }}>
            <Scheduler
              id="work-center-schedule"
              dataSource={soOperations}
              views={views}
              maxAppointmentsPerCell={"unlimited"}
              defaultCurrentView={"timelineMonth"}
              defaultCurrentDate={currentDate}
              height={schedulerHeight}
              showCurrentTimeIndicator={true}
              groups={soGroups}
              cellDuration={60}
              firstDayOfWeek={1}
              startDayHour={8}
              endDayHour={17}
              textExpr={"appointmentHeader"}
              startDateExpr={"opStartDateTime"}
              endDateExpr={"opFinishDateTime"}
            >
             <Resource
                fieldExpr={"orderNo"}
                allowMultiple={false}
                dataSource={shopOrders}
                label={"ShopOrder"}
                useColorAsDefault={true}
              />
              <Resource
                fieldExpr={"workCenterNo"}
                allowMultiple={true}
                dataSource={workCenters}
                label={"WorkCenter"}
              />
            </Scheduler>
          </div>
        </main>
      </div>
    );
  }
}

export default withStyles(styles)(WorkCenterSchedule);
