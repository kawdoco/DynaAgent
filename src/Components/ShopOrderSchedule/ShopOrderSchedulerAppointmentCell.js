import React from "react";

class ShopOrderSchedulerAppointmentCell extends React.PureComponent {
  render() {
    return (
      <div>
        <div className="appointmentTextHeader">{this.props.operationId}</div>
      </div>
    );
  }
}
export default ShopOrderSchedulerAppointmentCell;
