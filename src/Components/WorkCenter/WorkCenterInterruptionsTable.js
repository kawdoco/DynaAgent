import React from "react";
import Button from "devextreme-react/button";
import DataGrid, {
  Column,
  Editing,
  Paging,
  Lookup
} from "devextreme-react/data-grid";
import { interruptWorkCenter } from "../../Services/WorkCenterService";
import { getLocalDate } from "../../Global/DateTimeUtil";

class WorkCenterInterruptionsTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = { events: [] };
  }

  onInitNewRow = event => {
    event.data.workCenterNo = this.props.workCenterDetails.workCenterNo
  };

  onRowInserting = event => {
    let interruptionDetail = {
        "id": 0,
        "workCenterNo": event.data.workCenterNo,
        "interruptionFromDateTime": getLocalDate(event.data.interruptionFromDateTime),
        "interruptionToDateTime": getLocalDate(event.data.interruptionToDateTime)
    };
    interruptWorkCenter(interruptionDetail).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onRowUpdating = event => {
    let interruptionDetail = {
        "id": event.data.id,
        "workCenterNo": event.data.workCenterNo,
        "interruptionFromDateTime": event.data.interruptionFromDateTime,
        "interruptionToDateTime": event.data.interruptionToDateTime
    };
  };

  render() {
    return (
      <React.Fragment>
        <DataGrid
          id={"gridContainer"}
          dataSource={this.props.workCenterInterruptions}
          keyExpr={"id"}
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

          <Column dataField={"id"} />
          <Column dataField={"workCenterNo"} />
          <Column dataField={"interruptionFromDateTime"} dataType="datetime" />
          <Column dataField={"interruptionToDateTime"} dataType="datetime" />
        </DataGrid>
      </React.Fragment>
    );
  }
}

export default WorkCenterInterruptionsTable;
