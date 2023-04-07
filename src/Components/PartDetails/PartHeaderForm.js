import React from "react";
import PropTypes from "prop-types";
import classNames from "classnames";
import { withStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import SaveIcon from "@material-ui/icons/Save";
import { Row, Col } from "react-grid-system";
import { addPart, updatePart } from "../../Services/PartDetailsService";

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

class PartHeaderForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "Cat in the Hat",
      age: "",
      multiline: "Controlled"
    };
  }

  handleChange = name => event => {
    this.setState({
      [name]: event.target.value
    });
  };

  componentWillReceiveProps(nextProps){
    this.setState({
      id: nextProps.partDetails.id,
      partNo: nextProps.partDetails.partNo,
      partDescription: nextProps.partDetails.partDescription,
      vendor: nextProps.partDetails.vendor,
    })
  }


  onAdd = () => {
    let partDetails = {
      "id": 0,
      "partNo": this.state.partNo,
      "partDescription": this.state.partDescription,
      "vendor": this.state.vendor
    };
    addPart(partDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onUpdate = () => {
    let partDetails = {
      "id": this.state.id,
      "partNo": this.state.partNo,
      "partDescription": this.state.partDescription,
      "vendor": this.state.vendor
    };
    updatePart(partDetails).then(res => {
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
            <div style={{ alignContent: "center" }}>
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
              id="part-id"
              label="Part ID"
              defaultValue=" "
              value={this.state.id}
              className={classes.textField}
              onChange={this.handleChange("id")}
              margin="normal"
              variant="standard"
              disabled
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
              id="part-description"
              label="Part Type"
              defaultValue=" "
              value={this.state.partDescription}
              className={classes.textField}
              onChange={this.handleChange("partDescription")}
              margin="normal"
              variant="standard"
            />
            <TextField
              id="part-vendor"
              label="Vendor"
              defaultValue=" "
              value={this.state.vendor}
              className={classes.textField}
              onChange={this.handleChange("vendor")}
              margin="normal"
              variant="standard"
            />
          </form>
        </Row>
      </div>
    );
  }
}

PartHeaderForm.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(PartHeaderForm);
