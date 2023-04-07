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
import { Row, Col } from "react-grid-system";
import { addWorkCenter, updateWorkCenter } from "../../Services/WorkCenterService";

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
    width: "100%"
  }
});

class WorkCenterHeaderForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentWillReceiveProps(nextProps){
    this.setState({
      id: nextProps.workCenterDetails.id,
      workCenterNo: nextProps.workCenterDetails.workCenterNo,
      workCenterType: nextProps.workCenterDetails.workCenterType,
      workCenterDescription: nextProps.workCenterDetails.workCenterDescription,
      workCenterCapacity: nextProps.workCenterDetails.workCenterCapacity
    })
  }

  handleChange = name => event => {
    this.setState({
      [name]: event.target.value
    });
  };

  onAdd = () => {
    let workCenterDetails = {
      "id": 0,
      "workCenterNo": this.state.workCenterNo,
      "workCenterType":this.state.workCenterType,
      "workCenterDescription": this.state.workCenterDescription,
      "workCenterCapacity": this.state.workCenterCapacity
    };
    addWorkCenter(workCenterDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onUpdate = () => {
    let workCenterDetails = {
      "id": this.state.id,
      "workCenterNo": this.state.workCenterNo,
      "workCenterType":this.state.workCenterType,
      "workCenterDescription": this.state.workCenterDescription,
      "workCenterCapacity": this.state.workCenterCapacity
    };
    updateWorkCenter(workCenterDetails).then(res => {
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
          <Col md={10} />
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
                aria-label="Add"
                className={classes.fab}
                onClick={this.onUpdate}
              >
                <SaveIcon />
              </Fab>
            </div>
          </Col>
        </Row>
        <Row>
          <form className={classes.container} noValidate autoComplete="off">
            <TextField
              id="work-center-id"
              label="ID"
              defaultValue=" "
              value={this.state.id}
              className={classes.textField}
              margin="normal"
              variant="standard"
              disabled
            />
            <TextField
              id="work-center-no"
              label="Work Center No"
              defaultValue=" "
              value={this.state.workCenterNo}
              className={classes.textField}
              onChange={this.handleChange("workCenterNo")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="work-center-type"
              label="Work Center Type"
              defaultValue=" "
              value={this.state.workCenterType}
              className={classes.textField}
              onChange={this.handleChange("workCenterType")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="work-center-description"
              label="Description"
              defaultValue=" "
              value={this.state.workCenterDescription}
              className={classes.textField}
              onChange={this.handleChange("workCenterDescription")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="work-center-capacity"
              label="Capacity Type"
              defaultValue=" "
              value={this.state.workCenterCapacity}
              className={classes.textField}
              onChange={this.handleChange("workCenterCapacity")}
              margin="normal"
              variant="standard"
            />
          </form>
        </Row>
      </div>
    );
  }
}

WorkCenterHeaderForm.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(WorkCenterHeaderForm);
