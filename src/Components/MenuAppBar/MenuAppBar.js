import React from "react";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import AccountCircle from "@material-ui/icons/AccountCircle";
import MenuItem from "@material-ui/core/MenuItem";
import Menu from "@material-ui/core/Menu";
import InputBase from "@material-ui/core/InputBase";
import SearchIcon from "@material-ui/icons/Search";
import TextField from "@material-ui/core/TextField";
import { fade } from "@material-ui/core/styles/colorManipulator";
import { Container, Row, Col } from "react-grid-system";

const styles = theme => ({
  root: {
    flexGrow: 1
  },
  grow: {
    flexGrow: 1,
    marginLeft: 75
  },
  search: {
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(theme.palette.common.white, 0.15),
    "&:hover": {
      backgroundColor: fade(theme.palette.common.white, 0.25)
    },
    marginRight: theme.spacing.unit * 2,
    marginLeft: 0,
    width: "100%",
    [theme.breakpoints.up("sm")]: {
      marginLeft: theme.spacing.unit * 3,
      width: "auto"
    }
  },
  searchIcon: {
    width: theme.spacing.unit * 9,
    height: "100%",
    position: "absolute",
    pointerEvents: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center"
  },
  inputRoot: {
    color: "inherit",
    width: "100%"
  },
  inputInput: {
    paddingTop: theme.spacing.unit,
    paddingRight: theme.spacing.unit,
    paddingBottom: theme.spacing.unit,
    paddingLeft: theme.spacing.unit * 10,
    transition: theme.transitions.create("width"),
    width: "100%",
    [theme.breakpoints.up("md")]: {
      width: 200
    }
  },
  selectorContainer: {
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    marginBottom: 10
  },
  selectorTextContainer: {
    marginTop: 25,
    marginLeft: 25
  },
  fab: {
    margin: theme.spacing.unit
  },
  selector: {
    display: "inline-block"
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200
  }
});

class MenuAppBar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      auth: true,
      anchorEl: null,
      orderno: 1
    };
  }

  handleChange = event => {
    this.setState({ auth: event.target.checked });
  };

  handleMenu = event => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  renderSwitch(param) {
    const { classes } = this.props;
    switch (param) {
      case "Shop Order":
        return (
          <Container>
            <Row>
              <Col>
                <div className={classes.selectorTextContainer}>
                  <label>Order No</label>
                </div>
              </Col>
              <Col>
                <div className={classes.selectorContainer}>
                  <div className={classes.selector}>
                    <TextField
                      id="standard-select-orderno"
                      select
                      className={classes.textField}
                      SelectProps={{
                        MenuProps: {
                          className: classes.menu
                        }
                      }}
                      value={this.props.currentOrderNo}
                      onChange={this.props.orderNoChangedEvent("orderno")}
                      margin="normal"
                      variant="standard"
                    >
                      {this.props.shopOrderIds.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </div>
                </div>
              </Col>
            </Row>
          </Container>
        );
      case "Work Center":
        return (
          <Container>
            <Row>
              <Col>
                <div className={classes.selectorTextContainer}>
                  <label>Work Center No</label>
                </div>
              </Col>
              <Col>
                <div className={classes.selectorContainer}>
                  <div className={classes.selector}>
                    <TextField
                      id="standard-select-workcenterno"
                      select
                      className={classes.textField}
                      SelectProps={{
                        MenuProps: {
                          className: classes.menu
                        }
                      }}
                      value={this.props.currentWorkCenterNo}
                      onChange={this.props.workCenterNoChangedEvent(
                        "workcenterno"
                      )}
                      margin="normal"
                      variant="standard"
                    >
                      {this.props.workCenterNos.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </div>
                </div>
              </Col>
            </Row>
          </Container>
        );
      case "Part Details":
        return (
          <Container>
            <Row>
              <Col>
                <div className={classes.selectorTextContainer}>
                  <label>Part No</label>
                </div>
              </Col>
              <Col>
                <div className={classes.selectorContainer}>
                  <div className={classes.selector}>
                    <TextField
                      id="standard-select-partno"
                      select
                      className={classes.textField}
                      SelectProps={{
                        MenuProps: {
                          className: classes.menu
                        }
                      }}
                      value={this.props.currentPartNo}
                      onChange={this.props.partNoChangedEvent(
                        "partno"
                      )}
                      margin="normal"
                      variant="standard"
                    >
                      {this.props.partNos.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </div>
                </div>
              </Col>
            </Row>
          </Container>
        );
      default:
        return null;
    }
  }

  render() {
    const { classes } = this.props;
    const { auth, anchorEl, orderNos, orderno } = this.state;
    const open = Boolean(anchorEl);

    return (
      <div className={classes.root}>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" color="inherit" className={classes.grow}>
              {this.props.titleText}
            </Typography>
            {this.renderSwitch(this.props.titleText)}
            <div className={classes.search}>
              <div className={classes.searchIcon}>
                <SearchIcon />
              </div>
              <InputBase
                placeholder="Search…"
                classes={{
                  root: classes.inputRoot,
                  input: classes.inputInput
                }}
              />
            </div>
            {auth && (
              <div>
                <IconButton
                  aria-owns={open ? "menu-appbar" : undefined}
                  aria-haspopup="true"
                  onClick={this.handleMenu}
                  color="inherit"
                >
                  <AccountCircle />
                </IconButton>
                <Menu
                  id="menu-appbar"
                  anchorEl={anchorEl}
                  anchorOrigin={{
                    vertical: "top",
                    horizontal: "right"
                  }}
                  transformOrigin={{
                    vertical: "top",
                    horizontal: "right"
                  }}
                  open={open}
                  onClose={this.handleClose}
                >
                  <MenuItem onClick={this.handleClose}>Profile</MenuItem>
                  <MenuItem onClick={this.handleClose}>My account</MenuItem>
                </Menu>
              </div>
            )}
          </Toolbar>
        </AppBar>
      </div>
    );
  }
}

MenuAppBar.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(MenuAppBar);
