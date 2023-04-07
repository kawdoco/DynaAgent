import React from "react";
import Button from "devextreme-react/button";
import DataGrid, {
  Column,
  Editing,
  Paging,
  Lookup
} from "devextreme-react/data-grid";
import { addShopOrderOperation, updateShopOrderOperation } from "../../Services/ShopOrderService";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = { events: [] };
  }

  onRowInserting = event => {
    let shopOrderOperation = {
        "orderNo": event.data.orderNo,
        "operationNo": event.data.operationNo,
        "workCenterNo": event.data.workCenterNo,
        "workCenterType": event.data.workCenterType,
        "operationDescription": event.data.operationDescription,
        "operationSequence": event.data.operationSequence,
        "precedingOperationId": event.data.precedingOperationId,
        "workCenterRuntimeFactor": event.data.workCenterRuntimeFactor,
        "workCenterRuntime": event.data.workCenterRuntime,
        "laborRuntimeFactor": event.data.laborRuntimeFactor,
        "laborRunTime": event.data.laborRunTime,
        "opStartDateTime": event.data.opStartDateTime,
        "opFinishDateTime": event.data.opFinishDateTime,
        "quantity": event.data.quantity,
        "operationStatus": event.data.operationStatus,
    };
    addShopOrderOperation(shopOrderOperation).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onRowUpdating = event => {
    let shopOrderOperation = {
      "operationId": event.data.operationId,
      "orderNo": event.data.orderNo,
      "operationNo": event.data.operationNo,
      "workCenterNo": event.data.workCenterNo,
      "workCenterType": event.data.workCenterType,
      "operationDescription": event.data.operationDescription,
      "operationSequence": event.data.operationSequence,
      "precedingOperationId": event.data.precedingOperationId,
      "workCenterRuntimeFactor": event.data.workCenterRuntimeFactor,
      "workCenterRuntime": event.data.workCenterRuntime,
      "laborRuntimeFactor": event.data.laborRuntimeFactor,
      "laborRunTime": event.data.laborRunTime,
      "opStartDateTime": event.data.opStartDateTime,
      "opFinishDateTime": event.data.opFinishDateTime,
      "quantity": event.data.quantity,
      "operationStatus": event.data.operationStatus
    };
    updateShopOrderOperation(shopOrderOperation).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  render() {
    return (
      <React.Fragment>
        <DataGrid
          id={"gridContainer"}
          dataSource={this.props.shopOrderOperations}
          keyExpr={"operationId"}
          allowColumnReordering={true}
          showBorders={true}
          onEditingStart={this.onEditingStart}
          onInitNewRow={this.onInitNewRow}
          onRowInserting={this.onRowInserting}
          onRowInserted={this.onRowInserted}
          onRowUpdating={this.onRowUpdating}
          onRowUpdated={this.onRowUpdated}
          onRowRemoving={this.onRowRemoving}
          onRowRemoved={this.onRowRemoved}
        >
          <Paging enabled={true} />
          <Editing
            mode={"row"}
            allowUpdating={true}
            allowDeleting={true}
            allowAdding={true}
          />

          <Column dataField={"operationId"} />
          <Column dataField={"orderNo"} />
          <Column dataField={"operationNo"} />
          <Column dataField={"operationSequence"} />
          <Column dataField={"operationDescription"} />
          <Column dataField={"precedingOperationId"} />
          <Column dataField={"workCenterRuntimeFactor"} />
          <Column dataField={"workCenterRuntime"} />
          <Column dataField={"laborRuntimeFactor"} />
          <Column dataField={"laborRunTime"} />
          <Column dataField={"opStartDateTime"} dataType={"datetime"} />
          <Column dataField={"opFinishDateTime"} dataType={"datetime"} />
          <Column dataField={"quantity"} />
          <Column dataField={"workCenterType"} />
          <Column dataField={"workCenterNo"} />
          <Column dataField={"operationStatus"} />
        </DataGrid>
      </React.Fragment>
    );
  }
}

export default App;
