import React from "react";
import PropTypes from "prop-types";
import classNames from "classnames";
import { withStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import SaveIcon from "@material-ui/icons/Save";
import AvTimer from "@material-ui/icons/AvTimer";
import CancelIcon from "@material-ui/icons/Cancel";
import { Row, Col } from "react-grid-system";
import {
  addShopOrder,
  updateShopOrder,
  changeOpStatusToUnschedule,
  cancelShoporder
} from "../../Services/ShopOrderService";

const styles = theme => ({
  container: {
    display: "flex",
    flexWrap: "wrap",
    marginLeft: theme.spacing.unit
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
    color: "red"
  },
  inputColor: {
    color: "white"
  },
  dense: {
    marginTop: 19
  },
  menu: {
    width: 200
  },
  fabContainer: {
    width: "200%"
  }
});

class ShopOrderHeaderForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "Cat in the Hat",
      age: "",
      multiline: "Controlled",
      currency: "EUR",
      currentShopOrder: {}
    };
  }

  handleChange = name => event => {
    this.setState({
      [name]: event.target.value
    });
  };

  componentWillReceiveProps(nextProps) {
    this.setState({
      id: nextProps.shopOrderDetails.id,
      orderNo: nextProps.shopOrderDetails.orderNo,
      description: nextProps.shopOrderDetails.description,
      createdDate: nextProps.shopOrderDetails.createdDate,
      partNo: nextProps.shopOrderDetails.partNo,
      structureRevision: nextProps.shopOrderDetails.structureRevision,
      routingRevision: nextProps.shopOrderDetails.routingRevision,
      requiredDate: nextProps.shopOrderDetails.requiredDate,
      startDate: nextProps.shopOrderDetails.startDate,
      finishDate: nextProps.shopOrderDetails.finishDate,
      schedulingDirection: nextProps.shopOrderDetails.schedulingDirection,
      customerNo: nextProps.shopOrderDetails.customerNo,
      schedulingStatus: nextProps.shopOrderDetails.schedulingStatus,
      shopOrderStatus: nextProps.shopOrderDetails.shopOrderStatus,
      priority: nextProps.shopOrderDetails.priority,
      revenueValue: nextProps.shopOrderDetails.revenueValue
    });
  }

  onAdd = () => {
    let shopOrderDetails = {
      id: 0,
      orderNo: this.state.orderNo,
      description: this.state.description,
      createdDate: this.state.createdDate,
      partNo: this.state.partNo,
      structureRevision: this.state.structureRevision,
      routingRevision: this.state.routingRevision,
      requiredDate: this.state.requiredDate,
      startDate: this.state.startDate,
      finishDate: this.state.finishDate,
      schedulingDirection: this.state.schedulingDirection,
      customerNo: this.state.customerNo,
      schedulingStatus: this.state.schedulingStatus,
      shopOrderStatus: this.state.shopOrderStatus,
      priority: this.state.priority,
      revenueValue: this.state.revenueValue
    };
    addShopOrder(shopOrderDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onUpdate = () => {
    let shopOrderDetails = {
      id: this.state.id,
      orderNo: this.state.orderNo,
      description: this.state.description,
      createdDate: this.state.createdDate,
      partNo: this.state.partNo,
      structureRevision: this.state.structureRevision,
      routingRevision: this.state.routingRevision,
      requiredDate: this.state.requiredDate,
      startDate: this.state.startDate,
      finishDate: this.state.finishDate,
      schedulingDirection: this.state.schedulingDirection,
      customerNo: this.state.customerNo,
      schedulingStatus: this.state.schedulingStatus,
      shopOrderStatus: this.state.shopOrderStatus,
      priority: this.state.priority,
      revenueValue: this.state.revenueValue
    };
    updateShopOrder(shopOrderDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onChangeOpStatusToUnschedule = () => {
    let shopOrderDetails = {
      id: this.state.id,
      orderNo: this.state.orderNo,
      description: this.state.description,
      createdDate: this.state.createdDate,
      partNo: this.state.partNo,
      structureRevision: this.state.structureRevision,
      routingRevision: this.state.routingRevision,
      requiredDate: this.state.requiredDate,
      startDate: this.state.startDate,
      finishDate: this.state.finishDate,
      schedulingDirection: this.state.schedulingDirection,
      customerNo: this.state.customerNo,
      schedulingStatus: this.state.schedulingStatus,
      shopOrderStatus: this.state.shopOrderStatus,
      priority: this.state.priority,
      revenueValue: this.state.revenueValue
    };

    changeOpStatusToUnschedule(shopOrderDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onCancelShopOrder = () => {
    cancelShoporder(this.state.orderNo).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  render() {
    const { classes } = this.props;
    return (
      <div className={classes.fabContainer}>
        <Row>
          <Col md={8} />
          <Col md={1}>
            <div style={{ marginRight: 5 }}>
              <Fab
                color="secondary"
                aria-label="Add"
                className={classes.fab}
                onClick={this.onAdd}
              >
                <AddIcon />
              </Fab>
            </div>
          </Col>
          <Col md={1}>
            <div style={{ alignContent: "center" }}>
              <Fab
                color="secondary"
                aria-label="Update"
                className={classes.fab}
                onClick={this.onUpdate}
              >
                <SaveIcon />
              </Fab>
            </div>
          </Col>
          <Col md={1}>
            <div style={{ alignContent: "center" }}>
              <Fab
                color="secondary"
                aria-label="Unschedule"
                className={classes.fab}
                onClick={this.onChangeOpStatusToUnschedule}
              >
                <AvTimer />
              </Fab>
            </div>
          </Col>
          <Col md={1}>
            <div style={{ alignContent: "center" }}>
              <Fab
                color="secondary"
                aria-label="Cancel"
                className={classes.fab}
                onClick={this.onCancelShopOrder}
              >
                <CancelIcon />
              </Fab>
            </div>
          </Col>
        </Row>
        <Row>
          <form className={classes.container} noValidate autoComplete="off">
            <TextField
              id="order-id"
              label="Order ID"
              defaultValue=" "
              value={this.state.id}
              className={classes.textField}
              onChange={this.handleChange("id")}
              margin="normal"
              variant="standard"
              disabled
            />
            <TextField
              id="order-no"
              label="Order No"
              defaultValue=" "
              value={this.state.orderNo}
              className={classes.textField}
              onChange={this.handleChange("orderNo")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="description"
              label="Description"
              defaultValue=" "
              value={this.state.description}
              className={classes.textField}
              onChange={this.handleChange("description")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="created-date"
              label="Created Date"
              type="date"
              defaultValue="2000-01-01"
              value={this.state.createdDate}
              className={classes.textField}
              onChange={this.handleChange("createdDate")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="part-no"
              label="Part No"
              defaultValue=" "
              value={this.state.partNo}
              className={classes.textField}
              onChange={this.handleChange("partNo")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="structure-revision"
              label="Structure Revision"
              defaultValue=" "
              value={this.state.structureRevision}
              className={classes.textField}
              onChange={this.handleChange("structureRevision")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="routing-revision"
              label="Routing Revision"
              defaultValue=" "
              value={this.state.routingRevision}
              className={classes.textField}
              onChange={this.handleChange("routingRevision")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="required-date"
              label="Required Date"
              type="date"
              defaultValue="2000-01-01"
              value={this.state.requiredDate}
              className={classes.textField}
              onChange={this.handleChange("requiredDate")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="start-date"
              label="Start Date"
              type="date"
              defaultValue="2000-01-01"
              value={this.state.startDate}
              className={classes.textField}
              onChange={this.handleChange("startDate")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="finish-date"
              label="Finish Date"
              type="date"
              defaultValue="2000-01-01"
              value={this.state.finishDate}
              className={classes.textField}
              onChange={this.handleChange("finishDate")}
              margin="normal"
              variant="standard"
            />
            <TextField
              select
              id="scheduling-direction"
              label="Scheduling Direction"
              InputProps={{
                startAdornment: (
                  <InputAdornment variant="filled" position="end" />
                )
              }}
              className={classes.textField}
              SelectProps={{
                MenuProps: {
                  className: classes
                }
              }}
              value={this.state.schedulingDirection}
              onChange={this.handleChange("schedulingDirection")}
              margin="normal"
              variant="standard"
            >
              {this.props.schedulingDirectionList.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              id="customer-no"
              label="Customer No"
              defaultValue=" "
              value={this.state.customerNo}
              className={classes.textField}
              onChange={this.handleChange("customerNo")}
              margin="normal"
              variant="standard"
            />
            <TextField
              select
              id="scheduling-status"
              label="Scheduling Status"
              InputProps={{
                startAdornment: (
                  <InputAdornment variant="filled" position="end" />
                )
              }}
              className={classes.textField}
              SelectProps={{
                MenuProps: {
                  className: classes
                }
              }}
              value={this.state.schedulingStatus}
              onChange={this.handleChange("schedulingStatus")}
              margin="normal"
              variant="standard"
            >
              {this.props.schedulingStatusList.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              id="shop-order-status"
              label="Shop Order Status"
              InputProps={{
                startAdornment: (
                  <InputAdornment variant="filled" position="end" />
                )
              }}
              className={classes.textField}
              SelectProps={{
                MenuProps: {
                  className: classes
                }
              }}
              value={this.state.shopOrderStatus}
              onChange={this.handleChange("shopOrderStatus")}
              margin="normal"
              variant="standard"
            >
              {this.props.shopOrderStatusList.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              id="priority"
              label="Priority"
              InputProps={{
                startAdornment: (
                  <InputAdornment variant="filled" position="end" />
                )
              }}
              className={classes.textField}
              SelectProps={{
                MenuProps: {
                  className: classes
                }
              }}
              value={this.state.priority}
              onChange={this.handleChange("priority")}
              margin="normal"
              variant="standard"
            >
              {this.props.priorityList.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              id="revenue-value"
              label="Revenue Value"
              InputProps={{
                startAdornment: (
                  <InputAdornment variant="filled" position="end" />
                )
              }}
              className={classes.textField}
              SelectProps={{
                MenuProps: {
                  className: classes
                }
              }}
              value={this.state.revenueValue}
              onChange={this.handleChange("priority")}
              margin="normal"
              variant="standard"
            >
              {this.props.revenueValueList.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </form>
        </Row>
      </div>
    );
  }
}

ShopOrderHeaderForm.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(ShopOrderHeaderForm);
