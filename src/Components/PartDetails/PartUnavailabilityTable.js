import React from "react";
import Button from "devextreme-react/button";
import DataGrid, {
  Column,
  Editing,
  Paging,
  Lookup
} from "devextreme-react/data-grid";
import { addPartUnavailability } from "../../Services/PartDetailsService";
import { getLocalDate } from "../../Global/DateTimeUtil";

class PartUnavailabilityTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = { events: [] };
  }

  onInitNewRow = event => {
    event.data.partNo = this.props.partDetails.partNo
  }

  onRowInserting = event => {
    let unavailabilityDetails = {
        "id": 0,
        "partNo": event.data.partNo,
        "unavailableFromDateTime": getLocalDate(event.data.unavailableFromDateTime),
        "unavailableToDateTime": getLocalDate(event.data.unavailableToDateTime),
    };

    addPartUnavailability(unavailabilityDetails).then(res => {
      // get the service data
      const serviceData = res.data;
      alert(serviceData);
    });
  };

  onRowUpdating = event => {
    let unavailabilityDetails = {
        "id": event.data.id,
        "partNo": event.data.partNo,
        "unavailableFromDateTime": getLocalDate(event.data.unavailableFromDateTime),
        "unavailableToDateTime": getLocalDate(event.data.unavailableToDateTime)
    };
  };

  render() {
    return (
      <React.Fragment>
        <DataGrid
          id={"gridContainer"}
          dataSource={this.props.partUnavailabilityDetails}
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
          <Column dataField={"partNo"} />
          <Column dataField={"unavailableFromDateTime"} dataType="datetime" />
          <Column dataField={"unavailableToDateTime"} dataType="datetime" />
        </DataGrid>
      </React.Fragment>
    );
  }
}

export default PartUnavailabilityTable;