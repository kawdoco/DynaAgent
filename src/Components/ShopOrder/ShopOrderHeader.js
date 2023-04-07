import React, { Component } from "react";
import PropTypes, { func } from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import { Container, Row, Col } from "react-grid-system";
import { formatDate } from "../../Global/DateTimeUtil";
/* CSS */
import "./ShopOrderHeader.css";

/* Components */
import ShopOrderHeaderForm from "./ShopOrderHeaderForm";
import MenuAppBar from "../MenuAppBar/MenuAppBar";
import ShopOrderOperationsTable from "../ShopOrder/ShopOrderOperationsTable";
import { getWCSchedule } from "../../Services/ShopOrderService";

const styles = theme => ({
  root: {
    flexGrow: 1
  },
  fab: {}
});

const schedulingDirectionList = [
  {
    value: "Forward",
    label: "Forward Direction"
  },
  {
    value: "Backward",
    label: "Backward Direction"
  }
];

const schedulingStatusList = [
  {
    value: "Unscheduled",
    label: "Unscheduled"
  },
  {
    value: "Scheduled",
    label: "Scheduled"
  }
];

const shopOrderStatusList = [
  {
    value: "Created",
    label: "Created"
  },
  {
    value: "InProgress",
    label: "In Progress"
  },
  {
    value: "Completed",
    label: "Completed"
  }
];

const priorityList = [
  {
    value: "Critical",
    label: "Critical"
  },
  {
    value: "High",
    label: "High"
  },
  {
    value: "Medium",
    label: "Medium"
  },
  {
    value: "Low",
    label: "Low"
  },
  {
    value: "Trivial",
    label: "Trivial"
  }
];

const revenueValueList = [
  {
    value: "5",
    label: "Very High"
  },
  {
    value: "4",
    label: "High"
  },
  {
    value: "3",
    label: "Medium"
  },
  {
    value: "2",
    label: "Low"
  },
  {
    value: "1",
    label: "Very Low"
  }
];

class ShopOrderHeader extends Component {
  constructor(props) {
    super(props);
    this.onShopOrderNoChanged = this.onShopOrderNoChanged.bind(this);
    this.state = {
      shopOrderNos: [],
      shopOrders: [],
      selectedOrderNo: 0,
      currentShopOrder: {}
    };
  }

  componentDidMount() {
    getWCSchedule().then(res => {
      // get the service data
      const serviceData = res.data;
      // send the service data to be formatted
      this.formatShopOrderOperationData(serviceData);
    });
    document.title = "Shop Order Details";
  }

  formatShopOrderOperationData(soOperationData) {
    var currentData;
    if (soOperationData != null) {
      currentData = soOperationData;
    }
    console.log(currentData);

    const shopOrderNos = [];
    const shopOrders = [];
    // Push first default order no.
    shopOrderNos.push({
      value: -1,
      label: "Select Order No"
    });
    for (var i = 0; i < currentData.length; i++) {
      var soObject = currentData[i];
      soObject.createdDate = formatDate(new Date(soObject.createdDate));
      soObject.requiredDate = formatDate(new Date(soObject.requiredDate));
      soObject.startDate = formatDate(new Date(soObject.startDate));
      soObject.finishDate = formatDate(new Date(soObject.finishDate));

      shopOrderNos.push({
        value: soObject.orderNo,
        label: soObject.orderNo
      });
      shopOrders.push(soObject);
    }

    console.log(shopOrderNos);

    this.setState({ shopOrderNos, shopOrders });
  }

  onShopOrderNoChanged = name => event => {
    var selectedOrderNo = event.target.value;
    this.setState({
      selectedOrderNo: selectedOrderNo
    });

    var filteredData = this.state.shopOrders.filter(
      field => field.orderNo == selectedOrderNo
    );
    console.log(filteredData[0]);
    var currentShopOrder = {};
    if (filteredData[0]) {
      currentShopOrder = filteredData[0];
    }
    this.setState({
      currentShopOrder: currentShopOrder
    });
  };

  render() {
    const { classes } = this.props;

    const {
      shopOrderNos,
      shopOrders,
      selectedOrderNo,
      currentShopOrder
    } = this.state;
    return (
      <div className="App">
        <MenuAppBar
          titleText="Shop Order"
          shopOrderIds={shopOrderNos}
          currentOrderNo={selectedOrderNo}
          orderNoChangedEvent={this.onShopOrderNoChanged}
        />

        <main id="page-wrap">
          <h1>Shop Order</h1>
          <div>
            <Row>
              <ShopOrderHeaderForm
                shopOrderDetails={currentShopOrder}
                schedulingDirectionList={schedulingDirectionList}
                schedulingStatusList={schedulingStatusList}
                shopOrderStatusList={shopOrderStatusList}
                priorityList={priorityList}
                revenueValueList={revenueValueList}
              />
            </Row>
            <Row>&nbsp;</Row>
            <Row>
              <ShopOrderOperationsTable shopOrderOperations={currentShopOrder.operations}/>
            </Row>
          </div>
        </main>
      </div>
    );
  }
}

ShopOrderHeader.propTypes = {
  classes: PropTypes.object.isRequired
};
export default withStyles(styles)(ShopOrderHeader);
