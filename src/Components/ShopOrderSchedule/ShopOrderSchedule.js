import React, { Component } from "react";
import Scheduler, { Resource } from "devextreme-react/scheduler";
import { withStyles } from "@material-ui/core/styles";
import { Row } from "react-grid-system";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { shopOrderOperations } from "../../Services/test";
import {
  getScheduledOrders,
  getWCScheduleTest
} from "../../Services/ShopOrderService";
import ShopOrderSchedulerAppointmentCell from "./ShopOrderSchedulerAppointmentCell";

/* CSS */
import "./ShopOrderSchedule.css";
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

const soGroups = ["orderNo"];
const groups = ["priority"];

class ShopOrderSchedule extends Component {
  constructor(props) {
    super(props);
    this.state = {
      schedulerHeight : 300,
      fromShopOrderID : 1,
      toShopOrderID : 10
    };
  }

  componentDidMount() {
    this.loadShopOrderOperationsBySkipTake();
    document.title = "Shop Order Schedule";
  }

  loadShopOrderOperationsBySkipTake(){
    var skip = this.state.fromShopOrderID - 1;
    var take = this.state.toShopOrderID - skip;

    getScheduledOrders(skip, take).then(res => {
      // get the service data
      const serviceData = res.data;
      // send the service data to be formatted
      this.formatShopOrderOperationData(serviceData);
    });

  }

  formatShopOrderOperationData(soOperationData){
    var currentData; 
    if (soOperationData == null){
      currentData = shopOrderOperations;
    }
    else{
      console.log("NOT NULL!")
      currentData = soOperationData;
    }
    console.log(currentData);

    const soOperations = [];
    const shopOrders = [];
    const workCenters = [];
    
    // the height of the scheduler is set dynamically, for each row 400 height is set
    var height = currentData.length * 150;
    this.setState({ schedulerHeight : height });

    for (var i = 0; i < currentData.length; i++) {
      var soObject = currentData[i];
      let shopOrder = {
        id: soObject.orderNo,
        text: "O" +soObject.orderNo + " : " + soObject.description,
        color: this.getRandomColor()
      };
      shopOrders.push(shopOrder);
      for (var j = 0; j < soObject.operations.length; j++) {
        var operationObj = soObject.operations[j];
        operationObj.appointmentHeader = "OP No: " + operationObj.operationId + ", WC No: " + operationObj.workCenterNo;
        soOperations.push(operationObj);

        let workCenter = {
          id: operationObj.workCenterNo,
          text: "Work Center " + operationObj.workCenterNo + " : " + operationObj.workCenterType,
          color: this.getRandomColor()
        };

        var found = workCenters.some(function (field) {
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

    this.setState({ soOperations, shopOrders, workCenters});
  }
  

  getRandomColor (){
    var letters = '0123456789ABCDEF';
    var color = '#';
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
    this.loadShopOrderOperationsBySkipTake();
  };

  render() {
    const { classes } = this.props;
    const { content, soOperations, shopOrders, workCenters, schedulerHeight } = this.state;
    return (
      <div className="App">
      <MenuAppBar titleText="Shop Order Schedule"/>
        <main id="page-wrap">
          <h1>Shop Order Schedule</h1>
          <div className={classes.searchContainer}>
            <Row>
              <form noValidate autoComplete="off">
                <TextField
                  id="from-shop-order"
                  label="From Shop Order ID"
                  defaultValue=" "
                  value={this.state.fromShopOrderID}
                  onChange={this.handleChange("fromShopOrderID")}
                  margin="normal"
                  variant="standard"
                />
                <TextField
                  id="to-shop-order"
                  label="To Shop Order ID"
                  defaultValue=" "
                  value={this.state.toShopOrderID}
                  onChange={this.handleChange("toShopOrderID")}
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
              //appointmentComponent={ShopOrderSchedulerAppointmentCell}
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
              />
              <Resource
                fieldExpr={"workCenterNo"}
                allowMultiple={true}
                dataSource={workCenters}
                label={"WorkCenter"}
                useColorAsDefault={true}
              />
            </Scheduler>
          </div>
        </main>
      </div>
    );
  }
}

export default  withStyles(styles)(ShopOrderSchedule);
