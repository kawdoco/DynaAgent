/*global gantt*/
import React, { Component } from "react";
import "dhtmlx-gantt";
import "dhtmlx-gantt/codebase/skins/dhtmlxgantt_material.css";

export default class Gantt extends Component {
  componentDidMount() {
    gantt.init(this.ganttContainer);
    gantt.addCalendar({
      id: "custom",
      worktime: {
        hours: [8, 17],
        days: [0, 1, 1, 1, 1, 1, 0]
      }
    });
    gantt.parse(this.props.tasks);
  }

  setZoom(value) {
    switch (value) {
      case "Hours":
        gantt.config.scale_unit = "day";
        gantt.config.date_scale = "%d %M";
        gantt.config.scale_height = 60;
        gantt.config.min_column_width = 30;
        gantt.config.subscales = [{ unit: "hour", step: 1, date: "%H" }];
        break;
      case "Days":
        gantt.config.min_column_width = 70;
        gantt.config.scale_unit = "week";
        gantt.config.date_scale = "#%W";
        gantt.config.subscales = [{ unit: "day", step: 1, date: "%d %M" }];
        gantt.config.scale_height = 60;
        break;
      case "Months":
        gantt.config.min_column_width = 70;
        gantt.config.scale_unit = "month";
        gantt.config.date_scale = "%F";
        gantt.config.scale_height = 60;
        gantt.config.subscales = [{ unit: "week", step: 1, date: "#%W" }];
        break;
      default:
        break;
    }
  }

  shouldComponentUpdate(nextProps) {
    return this.props.zoom !== nextProps.zoom;
  }

  componentDidUpdate() {
    gantt.render();
  }

  render() {
    this.setZoom(this.props.zoom);
    return (
      <div
        ref={input => {
          this.ganttContainer = input;
        }}
        style={{ width: "100%", height: "100%" }}
      />
    );
  }
}
